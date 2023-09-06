package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.service.StatisticalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/statistical")
@CrossOrigin("*")
public class StatisticalController {
    @Autowired
    private StatisticalService statisticalService;
    @GetMapping("get-top-sell-product")
    public ResponseEntity<?> getTopSellProduct(
            @RequestParam(value = "top",defaultValue = "3") Integer top,
            @RequestParam(value = "from",required = false) Long from,
            @RequestParam(value="to",required = false) Long to
            ) throws SQLException {
            return new ResponseEntity<>(ResponseDataTemplate.OK.data(statisticalService.getTopSell(top,from,to)).build(), HttpStatus.OK);
    }
    @GetMapping("get-profit")
    public ResponseEntity<?> getStatisticalReport(
            @RequestParam(value = "year",defaultValue = "2023") Integer top,
            @RequestParam(value = "from",required = false) Long from,
            @RequestParam(value="to",required = false) Long to
    ) throws SQLException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(statisticalService.getProfit(top)).build(), HttpStatus.OK);
    }
}
