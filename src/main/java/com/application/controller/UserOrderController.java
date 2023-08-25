package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.vnpay.IPNReq;
import com.application.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/user")
public class UserOrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/order")
    public ResponseEntity<?> getOrder(Authentication authentication){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.getOrder(authentication.getName())).build(), HttpStatus.OK);
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
