package com.application.controller;


import com.application.common.ResponseDataTemplate;
import com.application.constant.Constant;
import com.application.dto.request.OrderGhnReq;
import com.application.dto.request.OrderReq;
import com.application.service.OrderService;
import lombok.SneakyThrows;
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
    @GetMapping("get-all")
    public ResponseEntity<?> order(@RequestParam(value = "st",defaultValue = "-1") Integer status,
                                   @RequestParam(value = "page",defaultValue = "0") Integer page,
                                   @RequestParam(value = "size",defaultValue = "5") Integer size){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.getAll(status,page,size)).build(), HttpStatus.OK);
    }
    @GetMapping("confirm")
    public ResponseEntity<?> confirm(@RequestParam(value = "orderId") Integer oid ){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.confirm(oid)).build(), HttpStatus.OK);
    }
    @PostMapping("delivery")
    public ResponseEntity<?> delivery(@RequestBody() OrderGhnReq orderGhnReq ){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.delivery(orderGhnReq)).build(), HttpStatus.OK);
//        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }
    @GetMapping("delivering")
    public ResponseEntity<?> delivering(@RequestParam() Integer orderId ){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.delivering(orderId)).build(), HttpStatus.OK);
//        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }
    @GetMapping("success")
    public ResponseEntity<?> success(@RequestParam() Integer orderId ){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.success(orderId)).build(), HttpStatus.OK);
//        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }
    @GetMapping("cancel")
    public ResponseEntity<?> cancel(@RequestParam() Integer orderId ){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.cancel(orderId)).build(), HttpStatus.OK);
//        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }

}
