package com.application.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailReq {
    private  Integer id;
    private BigDecimal originalPrice;
    private  Integer quantity;
    private  BigDecimal priceSell;
    private  Integer orderId;
    private  Integer productId;

}
