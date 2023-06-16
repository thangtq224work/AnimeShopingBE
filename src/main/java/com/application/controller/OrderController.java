package com.application.controller;


import com.application.common.ResponseDataTemplate;
import com.application.constant.Constant;
import com.application.dto.request.OrderReq;
import com.application.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("get-all")
    public ResponseEntity<?> order(@RequestParam(value = "st",defaultValue = "-1") Integer status,
                                   @RequestParam(value = "page",defaultValue = "0") Integer page,
                                   @RequestParam(value = "size",defaultValue = "5") Integer size){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.getAll(status,page,size)).build(), HttpStatus.OK);
    }
}
