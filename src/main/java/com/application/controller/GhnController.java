package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.CalculateFeeReq;
import com.application.dto.request.LoginReq;
import com.application.dto.request.OrderGhnReq;
import com.application.service.GhnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class GhnController {
    @Autowired
    GhnService ghnService;
    @GetMapping("/get-province")
    @Cacheable(cacheManager = "addressCache", value = "province")
    public ResponseEntity<?> getProvince() {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(ghnService.getProvince()).build(), HttpStatus.OK);
    }
    @GetMapping("/get-district")
    public ResponseEntity<?> getDistrict(@RequestParam("provideId")Integer id) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(ghnService.getDistrict(id)).build(), HttpStatus.OK);
    }
    @GetMapping("/get-ward")
    public ResponseEntity<?> getWard(@RequestParam("districtId")Integer id) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(ghnService.getWard(id)).build(), HttpStatus.OK);
    }
    @GetMapping("/get-service")
    public ResponseEntity<?> getService(@RequestParam("districtId") Integer id) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(ghnService.getService(id)).build(), HttpStatus.OK);
    }
    @PostMapping("/calculate-fee")
    public ResponseEntity<?> calculateFee(@RequestBody CalculateFeeReq calculateFeeReq) {
        System.out.println(calculateFeeReq);
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(ghnService.calculate(calculateFeeReq)).build(), HttpStatus.OK);
    }
    @PostMapping("preview")
    public ResponseEntity<?> preview(@RequestBody() OrderGhnReq orderGhnReq ){
//        System.out.println(orderGhnReq);
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(ghnService.preview(orderGhnReq)).build(), HttpStatus.OK);
//        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }
}
