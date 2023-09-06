package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.vnpay.IPNReq;
import com.application.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@RestController
@RequestMapping("/api/user")
public class UserOrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("/order")
    public ResponseEntity<?> getOrder(Authentication authentication,
                                      @RequestParam(name = "from",required = false)@DateTimeFormat(pattern = "MM/dd/yyyy")Date from,
                                      @RequestParam(name = "to",required = false) @DateTimeFormat(pattern = "MM/dd/yyyy") Date to,
                                      @RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                      @RequestParam(name = "size",required = false,defaultValue = "5") Integer size
    ){
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page-1, size, sort);
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.getOrder(authentication.getName(),from,to,pageable)).build(), HttpStatus.OK);
    }
    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrder(@PathVariable("id") Integer id){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.getOrderById(id)).build(), HttpStatus.OK);
    }
    @GetMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam("oid")Integer id, @RequestParam(value = "lang",required = false,defaultValue = "en")String locale, HttpServletRequest request, Authentication authentication) throws UnsupportedEncodingException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.createPaymentOrder(id,authentication,locale,request)).build(),HttpStatus.OK);
    }
    @GetMapping("/payment")
    public ResponseEntity<?> payment(HttpServletRequest request) throws UnsupportedEncodingException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.confirmPayment(request)).build(),HttpStatus.OK);
    }
    @GetMapping("/query-payment")
    public ResponseEntity<?> queryPayment(HttpServletRequest request,@RequestParam("oid") Integer id) throws IOException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.queryPayment(id,request)).build(),HttpStatus.OK);
    }
    @GetMapping("/refund")
    public ResponseEntity<?> refund(@RequestParam("oid") Integer id,Authentication authentication,HttpServletRequest request) throws IOException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.refundPayment(id,authentication,request)).build(),HttpStatus.OK);
    }

}
