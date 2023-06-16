package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.request.OrderReq;
import com.application.dto.response.OrderResp;
import com.application.dto.response.ProductResp;
import com.application.entity.Account;
import com.application.entity.Order;
import com.application.entity.OrderDetail;
import com.application.exception.NotFoundException;
import com.application.repository.AccountRepo;
import com.application.repository.OrderDetailRepo;
import com.application.repository.OrderRepo;
import com.application.service.OrderService;
import com.application.service.ProductService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailRepo orderDetailRepo;
    @Override
    public int create(OrderReq orderReq) {
        Account account = accountRepo.findByUsername(orderReq.getUserId(), Constant.Status.ACTIVE).orElseThrow(()->new NotFoundException(String.format("Username %s not found",orderReq.getUserId())));
        List<ProductResp> list = productService.getProductInCartV2(orderReq.getList());
        BigDecimal totalPrice = list.stream().map(i->i.getPriceSell()).reduce(BigDecimal.ZERO,(total,item)-> total.add(item));
        Order order = new Order(orderReq,account,totalPrice);
        order.setStatus(Constant.OrderStatus.WAITING);
        order.setCustomerMoney(BigDecimal.ZERO);
        Order orderSaved = orderRepo.save(order);
        List<OrderDetail> orderDetails = list.stream().map(i->new OrderDetail(i,orderSaved)).collect(Collectors.toList());
        orderDetailRepo.saveAll(orderDetails);
        return 1;
    }

    @Override
    public PageData<OrderResp> getAll(Integer st, Integer p, Integer size) {
        Specification<Order> orderSpecification = (root, query, criteriaBuilder) -> {
            Predicate predicate = st == -1?criteriaBuilder.and():criteriaBuilder.equal(root.get("status"),st);
            return criteriaBuilder.and(predicate);
        };
        Pageable pageable = PageRequest.of(p,size, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Order> pageData = orderRepo.findAll(orderSpecification,pageable);
        List<OrderResp> orderResps = pageData.toList().stream().map(i->new OrderResp(i)).collect(Collectors.toList());
        return PageData.of(pageData,orderResps);
    }
}
