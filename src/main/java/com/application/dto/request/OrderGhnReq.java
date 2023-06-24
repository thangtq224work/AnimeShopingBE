package com.application.dto.request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderGhnReq {
    private Integer orderId;
    private Integer paymentType;
    private String note = "";
    private String requiredNote;
    private String clientOrderCode;
    private String toName;
    private String toPhone;
    private String toAddress;
    private String toWardCode;
    private Integer districtId;
    private BigDecimal codAmount;
    private String content = "";
    private Integer weight;
    private Integer length;
    private Integer width;
    private Integer height;
    private BigDecimal insuranceValue;
    private Integer serviceTypeId;
    private Integer paymentTypeId = 1 ; // ( 1 : seller pay || 2 : buyer pay )
    private List<OrderGhnReq.Product> items;
    @Data
    public static class Product{
        private String name;
        private Integer quantity;
        private BigDecimal price;

    }


}
