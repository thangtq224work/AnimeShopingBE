package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.request.OrderGhnReq;
import com.application.dto.request.OrderReq;
import com.application.dto.response.OrderResp;
import com.application.dto.response.ProductResp;
import com.application.dto.response.ghn.PreviewResp;
import com.application.entity.Account;
import com.application.entity.Order;
import com.application.entity.OrderDetail;
import com.application.entity.Product;
import com.application.exception.NotFoundException;
import com.application.exception.ParamInvalidException;
import com.application.inject.GhnBean;
import com.application.repository.AccountRepo;
import com.application.repository.OrderDetailRepo;
import com.application.repository.OrderRepo;
import com.application.repository.ProductRepo;
import com.application.service.GhnService;
import com.application.service.OrderService;
import com.application.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderDetailRepo orderDetailRepo;
    @Autowired
    private GhnBean ghnBean;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private GhnService ghnService;
    @Override
    public int create(OrderReq orderReq) {
        Account account = accountRepo.findByUsername(orderReq.getUserId(), Constant.Status.ACTIVE).orElseThrow(()->new NotFoundException(String.format("Username %s not found",orderReq.getUserId())));
        List<ProductResp> list = productService.getProductInCartV2(orderReq.getList());
        BigDecimal totalPrice = list.stream().map(i->i.getPriceSell().multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO,(total,item)-> total.add(item));
        Order order = new Order(orderReq,account,totalPrice);
        order.setStatus(Constant.OrderStatus.WAITING);
        order.setOrderCode("HD"+new Date().getTime()+((int)Math.random()*101)); // 0 to 100
        order.setCustomerMoney(BigDecimal.ZERO);
        Order orderSaved = orderRepo.save(order);
        List<OrderDetail> orderDetails = list.stream().map(i->new OrderDetail(i,orderSaved)).collect(Collectors.toList());
        orderDetailRepo.saveAll(orderDetails);
        return 1;
    }

    @Override
    public int confirm(Integer orderId) {
        Order orders = orderRepo.findById(orderId).orElseThrow(()->new NotFoundException("Order not found"));
        boolean isNotAvailable = orders.getOrderDetails().stream().anyMatch((i)->{
            return i.getQuantity() > i.getProduct().getQuantity();
        });
        if(isNotAvailable){
            throw new ParamInvalidException("Product not available");
        }
        orders.setStatus(Constant.OrderStatus.CONFIRMED);
        orderRepo.save(orders);
        orders.getOrderDetails().stream().forEach(i->{
            Product product = i.getProduct();
            product.setQuantity(product.getQuantity()-i.getQuantity());
            if(product.getQuantity() < 0){
                log.error("System has problem.please check again");
                product.setQuantity(0);
            }
            productRepo.save(product);
        });
        return 1;
    }

    @SneakyThrows
    @Override
    public PageData<OrderResp> getAll(Integer st, Integer p, Integer size){
        Specification<Order> orderSpecification = (root, query, criteriaBuilder) -> {
            Predicate predicate = st == -1?criteriaBuilder.and():criteriaBuilder.equal(root.get("status"),st);
            return criteriaBuilder.and(predicate);
        };
        Pageable pageable = PageRequest.of(p,size, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Order> pageData = orderRepo.findAll(orderSpecification,pageable);
        try {
            if(st != Constant.OrderStatus.WAITING || st != Constant.OrderStatus.SUCCESS || st != Constant.OrderStatus.CANCEL){
                updateOrderStatus(pageData.toList()); // sync with ghn
            }
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw  exception;
        }
        List<OrderResp> orderResps = pageData.toList().stream().map(i->new OrderResp(i)).collect(Collectors.toList());
        return PageData.of(pageData,orderResps);
    }

    @Override
    public int delivery(OrderGhnReq orderGhnReq) {
        Order order = orderRepo.findById(orderGhnReq.getOrderId()).orElseThrow(()->new NotFoundException("Order not found"));
        if(order.getStatus() != Constant.OrderStatus.CONFIRMED){
            throw new ParamInvalidException("Status invalid");
        }
        PreviewResp.Data obj = (PreviewResp.Data) ghnService.delivery(orderGhnReq);
        order.setGhnCode(obj.getOrderCode());
        order.setServiceFee(BigDecimal.valueOf(obj.getTotal()));
        order.setStatus(Constant.OrderStatus.WAITING_SHIPPING);
        orderRepo.save(order);
        return 1;
    }

    @Override
    public int delivering(Integer orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(()->new NotFoundException("Order not found"));
        if(order.getStatus() != Constant.OrderStatus.WAITING_SHIPPING){
            throw new ParamInvalidException("Status invalid");
        }
        order.setStatus(Constant.OrderStatus.SHIPPING);
        orderRepo.save(order);
        return 1;
    }

    @Override
    public int cancel(Integer orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(()->new NotFoundException("Order not found"));
        if(order.getStatus() != Constant.OrderStatus.WAITING && order.getStatus() != Constant.OrderStatus.CONFIRMED){
            throw new ParamInvalidException("Status invalid");
        }
        if(order.getStatus() == Constant.OrderStatus.CONFIRMED){
             List<Product> products =order.getOrderDetails().stream().map(item->{
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity()+item.getQuantity());
                return product;
            }).collect(Collectors.toList());
            productRepo.saveAll(products);
        }
        order.setStatus(Constant.OrderStatus.CANCEL);
        orderRepo.save(order);
        return 1;
    }

    @Override
    public int success(Integer orderId) {
        Order order = orderRepo.findById(orderId).orElseThrow(()->new NotFoundException("Order not found"));
        if(order.getStatus() != Constant.OrderStatus.SHIPPING){
            throw new ParamInvalidException("Status invalid");
        }
        order.setStatus(Constant.OrderStatus.SUCCESS);
        orderRepo.save(order);
        return 1;
    }
    private void updateOrderStatus(List<Order> list) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("token",ghnBean.getToken());
        RestTemplate template = new RestTemplate();

        for (Order o: list) {
            if(o.getStatus() == Constant.OrderStatus.SHIPPING || o.getStatus() == Constant.OrderStatus.WAITING_SHIPPING){
                Map<String,Object> map = new HashMap<>();
                map.put("order_code",o.getGhnCode());
                HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(map),headers);
                ResponseEntity<String> response = template.exchange(ghnBean.getGetOrderDetail(), HttpMethod.POST,entity,String.class);
                log.info(response.getBody());
                JsonNode node = mapper.readTree(response.getBody());
                String status = node.get("data").get("status").asText();
                log.info("status : {}",status);
                switch (status){
                    case "delivering", "picked":
                        if(o.getStatus() < Constant.OrderStatus.SHIPPING){
                            updateOrder(o,Constant.OrderStatus.SHIPPING);
                        }
                        break;
                    case "delivered" :
                        updateOrder(o,Constant.OrderStatus.SUCCESS);
                        break;
                    // undefined status
                }
            }
        }
    }
    private void updateOrder(Order oid,Integer status){
        oid.setStatus(status);
        orderRepo.save(oid);
    }

}
