package com.application.dto.request;

import lombok.Data;

@Data
public class CalculateFeeReq {
    private Integer totalPrice;
    private Integer districtId;
    private String wardCode;
    private Integer weight;
    private Integer serviceType;
}
