package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.request.OrderGhnReq;
import com.application.dto.request.OrderReq;
import com.application.dto.response.OrderResp;
import com.application.dto.response.ProductResp;
import com.application.dto.response.ghn.PreviewResp;
import com.application.entity.*;
import com.application.exception.InvalidException;
import com.application.exception.NotFoundException;
import com.application.exception.ParamInvalidException;
import com.application.inject.GhnBean;
import com.application.inject.VnPay;
import com.application.repository.AccountRepo;
import com.application.repository.OrderDetailRepo;
import com.application.repository.OrderRepo;
import com.application.repository.ProductRepo;
import com.application.service.GhnService;
import com.application.service.OrderService;
import com.application.service.ProductService;
import com.application.utils.VnPayUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
    @Autowired
    private VnPay vnPay;
    @Autowired
    private VnPayUtil vnPayUtil;
    @Override
    public int create(OrderReq orderReq) {
        Account account = accountRepo.findByUsername(orderReq.getUserId(), Constant.Status.ACTIVE).orElseThrow(()->new NotFoundException(String.format("Username %s not found",orderReq.getUserId())));
        List<ProductResp> list = productService.getProductInCartV2(orderReq.getList());
        BigDecimal totalPrice = list.stream().map(i->i.getPriceSell().multiply(BigDecimal.valueOf(i.getQuantity()))).reduce(BigDecimal.ZERO,(total,item)-> total.add(item));
        Order order = new Order(orderReq,account,totalPrice);
        order.setStatus(Constant.OrderStatus.WAITING);
        order.setOrderCode("HD"+new Date().getTime()+((int)Math.random()*101)); // 0 to 100
        order.setCustomerMoney(BigDecimal.ZERO);
        Order orderSaved = orderRepo.saveAndFlush(order); // gay loi
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
    public Object createPaymentOrder(Integer id, Authentication authentication, String locale, HttpServletRequest request) throws UnsupportedEncodingException {
        if(locale == null || locale != "en" || locale != "vn"){
            locale = "en";
        }
        if(authentication == null || authentication.getName() == null){
            throw new NotFoundException(String.format("Order %d not found",id));
        }
        Order order = orderRepo.getByIdAndUsername(authentication.getName(), id).orElseThrow(()->new NotFoundException(String.format("Order %d not found",id)));
        if(order.getCustomerMoney().add(order.getShippingFee()).compareTo(order.getTotalPrice()) >= 0){
            throw new ParamInvalidException("Invalid request");
        }
        if(order.getExpiredPayment() == null || order.getUrlPayment() == null || new Date().after(order.getExpiredPayment())){
            // expired payment : 15 minutes
            // just support vnp_currcode "VND"
            // orderType : undefined order type . see more on vnp docs
            String vnp_TxnRef = order.getOrderCode()+vnPayUtil.getRandomNumber(8);
            String message = String.format("%s paid the bill %s",authentication.getName(),order.getOrderCode());
            String paymentUrl = this.createOrderUrl(order.getId(),vnp_TxnRef,order.getTotalPrice().add(order.getShippingFee()).intValue(),locale,message,"150002",request,null,"VND");
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            calendar.add(Calendar.MINUTE,15);
            order.setExpiredPayment(calendar.getTime());
            order.setUrlPayment(paymentUrl);
            order.setPaymentCode(vnp_TxnRef);
            orderRepo.save(order);
            return mapper.createObjectNode().put("code", "00").put("message","success").put("data",paymentUrl);
        }
        return mapper.createObjectNode().put("code", "00").put("message","success").put("data",order.getUrlPayment());
//        return order.getUrlPayment();
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
    public PageData<OrderResp> getOrder(String user, Date from, Date to, Pageable pageable) {
//        String user = ((Principal)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
//        log.info(user);
        if(user == null || user.trim().length() == 0){
            throw new NotFoundException(String.format("User %d not found",user));
        }
        Page<Order> orders = null;

        if(from ==null || to == null || from.after(to)){
            orders = orderRepo.getByUsername(user,pageable);
        }else{
            // from
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(from);
            calendar.set(Calendar.HOUR,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            from = calendar.getTime();
            // to
            calendar.setTime(to);
            calendar.set(Calendar.HOUR,23);
            calendar.set(Calendar.MINUTE,59);
            calendar.set(Calendar.SECOND,59);
            to = calendar.getTime();
            orders = orderRepo.getByUsername(user,from,to,pageable);
        }
        List<OrderResp> orderResps = orders.toList().stream().map(i->new OrderResp(i)).collect(Collectors.toList());
        return PageData.of(orders,orderResps);
    }

    @Override
    public OrderResp getOrderById(Integer id) {
        Order order = orderRepo.findById(id).orElseThrow(()->new NotFoundException("Order not found"));
        return new OrderResp(order,1);
    }

    @Override
    public int confirmPayment(HttpServletRequest request) throws UnsupportedEncodingException {
//        try {


        Map<String,String> fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType"))
        {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash"))
        {
            fields.remove("vnp_SecureHash");
        }
        log.info("Payment : " + vnPayUtil.hashAllFields(fields));
        String signValue = vnPayUtil.hashAllFields(fields,vnPay.getVnp_HashSecret());
        if (signValue.equals(vnp_SecureHash))
        {
            String vnp_TxnRef = fields.get("vnp_TxnRef");
//            boolean checkOrderId = true; // vnp_TxnRef exists in your database
            Optional<Order> optional = orderRepo.getByPaymentCode(vnp_TxnRef);
            if(optional.isEmpty()){
                throw new InvalidException("Order not found");
            }
            Order order = optional.get();
            if(order.getCustomerMoney().compareTo(order.getTotalPrice().add(order.getShippingFee())) >= 0){
                throw new ParamInvalidException("Invalid request");
            }
            Long vnp_Amount =Long.valueOf(fields.get("vnp_Amount"));
//            boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).

            if(order.getTotalPrice().compareTo(BigDecimal.valueOf(vnp_Amount)) >= 0) {
                throw new InvalidException("Invalid Amount");
            }
//              boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)
//            if(checkOrderId)
//            {
//                if(checkAmount)
//                {
//                    if (checkOrderStatus)
//                    {
                        if ("00".equals(request.getParameter("vnp_ResponseCode")) || "07".equals(request.getParameter("vnp_ResponseCode")))
                        {
                            BigDecimal money = BigDecimal.valueOf(Double.valueOf(fields.get("vnp_Amount")+""));
                            money = money.divide(BigDecimal.valueOf(100));
                            order.setCustomerMoney(order.getCustomerMoney().add(money));
                            order.setTransactionNo(request.getParameter("vnp_TransactionNo"));
//                            order.setUrlPayment(null);
                            orderRepo.save(order);
                        }
                        else
                        {
                            // Here Code update PaymnentStatus = 2 into your Database
                            throw new InvalidException("Fail payment");
                        }
//                    }
//                }
//             }
        }
        else
        {
            throw new InvalidException("Invalid Checksum");
        }
//    }
//        catch (Exception exception){
//            log.warn(exception.getMessage());
//            throw new InvalidException("Invalid payment error");
//        }
        return 1;
    }
//        catch(Exception e)
//    {
////        out.print("{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}");
//    }

//    }
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

    @Override
    public int refundPayment(Integer id, Authentication authentication, HttpServletRequest request) throws IOException {
        String user = authentication.getName();
        Order order = orderRepo.getByIdAndUsername(user,id).orElseThrow(()->new InvalidException("Order not found"));
        if(order.getCustomerMoney().compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidException("Order invalid");
        }
        String vnp_RequestId = vnPayUtil.getRandomNumber(16);
        String vnp_Version = vnPay.getVnp_Version();
        String vnp_Command = "refund";
        String vnp_TmnCode = vnPay.getVnp_TmnCode();
//        02: Giao dịch hoàn trả toàn phần (vnp_TransactionType=02)
//        03: Giao dịch hoàn trả một phần (vnp_TransactionType=03)
        String vnp_TransactionType = "02";
        String vnp_TxnRef = order.getPaymentCode();
//        long amount = Integer.parseInt(req.getParameter("amount"))*100;
        long amount = order.getCustomerMoney().intValue()*100;
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Refund OrderId: " + order.getOrderCode();
        String vnp_TransactionNo = order.getTransactionNo(); //Assuming value of the parameter "vnp_TransactionNo" does not exist on your system.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_TransactionDate = formatter.format(order.getExpiredPayment());
        String vnp_CreateBy = user;

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = vnPayUtil.getIpAddress(request);

        ObjectNode vnp_Params =mapper.createObjectNode();
        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);

        if(vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty())
        {
            vnp_Params.put("vnp_TransactionNo", vnp_TransactionNo);
        }

        vnp_Params.put("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.put("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" +
                vnp_TransactionType + "|" + vnp_TxnRef + "|" + vnp_Amount + "|" + vnp_TransactionNo + "|"
                + vnp_TransactionDate + "|" + vnp_CreateBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;

        String vnp_SecureHash = vnPayUtil.hmacSHA512(vnPay.getVnp_HashSecret(), hash_Data.toString());

        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL (vnPay.getVnp_apiUrl());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());

        return 0;
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
    private String createOrderUrl(Integer id,String vnp_TxnRef,Integer amount, String locale, String orderInfor, String orderType, HttpServletRequest request,String bankcode,String vnp_CurrCode) throws UnsupportedEncodingException {
//        String vnp_TxnRef = vnPayUtil.getRandomNumber(8);
        String vnp_IpAddr = vnPayUtil.getIpAddress(request);
        amount = Integer.parseInt(amount+"") * 100;
        Map vnp_Params = new HashMap();
        vnp_Params.put("vnp_Version", vnPay.getVnp_Version());
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnPay.getVnp_TmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        if (bankcode != null && !bankcode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankcode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnPayUtil.deAccent(orderInfor));
        vnp_Params.put("vnp_OrderType", orderType);
        if (locale != null && !locale.isEmpty()) {
            vnp_Params.put("vnp_Locale", locale);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", vnPay.getVnp_Returnurl()+"/"+id);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        //Add Params of 2.1.0 Version
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        //Billing
//        vnp_Params.put("vnp_Bill_Mobile", req.getParameter("txt_billing_mobile"));
//        vnp_Params.put("vnp_Bill_Email", req.getParameter("txt_billing_email"));
//        String fullName = (req.getParameter("txt_billing_fullname")).trim();
//        if (fullName != null && !fullName.isEmpty()) {
//            int idx = fullName.indexOf(' ');
//            String firstName = fullName.substring(0, idx);
//            String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
//            vnp_Params.put("vnp_Bill_FirstName", firstName);
//            vnp_Params.put("vnp_Bill_LastName", lastName);
//
//        }
//        vnp_Params.put("vnp_Bill_Address", req.getParameter("txt_inv_addr1"));
//        vnp_Params.put("vnp_Bill_City", req.getParameter("txt_bill_city"));
//        vnp_Params.put("vnp_Bill_Country", req.getParameter("txt_bill_country"));
//        if (req.getParameter("txt_bill_state") != null && !req.getParameter("txt_bill_state").isEmpty()) {
//            vnp_Params.put("vnp_Bill_State", req.getParameter("txt_bill_state"));
//        }
//        // Invoice
//        vnp_Params.put("vnp_Inv_Phone", req.getParameter("txt_inv_mobile"));
//        vnp_Params.put("vnp_Inv_Email", req.getParameter("txt_inv_email"));
//        vnp_Params.put("vnp_Inv_Customer", req.getParameter("txt_inv_customer"));
//        vnp_Params.put("vnp_Inv_Address", req.getParameter("txt_inv_addr1"));
//        vnp_Params.put("vnp_Inv_Company", req.getParameter("txt_inv_company"));
//        vnp_Params.put("vnp_Inv_Taxcode", req.getParameter("txt_inv_taxcode"));
//        vnp_Params.put("vnp_Inv_Type", req.getParameter("cbo_inv_type"));
        //Build data to hash and querystring
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayUtil.hmacSHA512(vnPay.getVnp_HashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPay.getVnp_PayUrl() + "?" + queryUrl;
        return paymentUrl;
    }
    @Override
    public String queryPayment(Integer oid,HttpServletRequest request) throws IOException {
        Order order = orderRepo.findById(oid).orElseThrow(()->new NotFoundException("Order not found"));
        String vnp_TxnRef = order.getPaymentCode();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_TransDate = formatter.format(order.getExpiredPayment());
        String vnp_RequestId = vnPayUtil.getRandomNumber(8);
        String vnp_Command = "querydr";
        String vnp_TmnCode = vnPay.getVnp_TmnCode();
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        String vnp_Version = vnPay.getVnp_Version();

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_IpAddr = vnPayUtil.getIpAddress(request);
//        ObjectMapper mapper1 = mapper;
        ObjectNode node = mapper.createObjectNode();
                node.put("vnp_RequestId", vnp_RequestId).put("vnp_Version", vnp_Version)
                .put("vnp_Command", vnp_Command).put("vnp_TmnCode", vnp_TmnCode).put("vnp_TxnRef", vnp_TxnRef)
                .put("vnp_OrderInfo", vnp_OrderInfo).put("vnp_TransactionDate", vnp_TransDate)
        //vnp_Params.put("vnp_TransactionNo", vnp_TransactionNo);
                .put("vnp_CreateDate", vnp_CreateDate).put("vnp_IpAddr", vnp_IpAddr);
        String hash_Data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" + vnp_TxnRef + "|" + vnp_TransDate + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;
        String vnp_SecureHash = vnPayUtil.hmacSHA512(vnPay.getVnp_HashSecret(), hash_Data.toString());
        node.put("vnp_SecureHash", vnp_SecureHash);
        URL url = new URL (vnPay.getVnp_apiUrl());
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(node.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        log.info("nSending 'POST' request to URL : " + url);
        log.info("Post Data : " + node.toString());
        log.info("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        return response.toString();
    }


}
