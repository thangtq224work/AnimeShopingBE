package com.application.inject;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class GhnBean {
    @Value("${ghn.token}")
    private String token;
    @Value("${ghn.shopId}")
    private Integer shopId;
    @Value("${ghn.getProvince}")
    private String getProvince;
    @Value("${ghn.getDistrict}")
    private String getDistrict;
    @Value("${ghn.getWard}")
    private String getWard;
    @Value("${ghn.createOrder}")
    private String createOrder;
    @Value("${ghn.cancelOrder}")
    private String cancelOrder;
    @Value("${ghn.calculateFee}")
    private String calculateFee;
    @Value("${ghn.getStore}")
    private String getStore;
    @Value("${ghn.getService}")
    private String getService;
    @Value("${ghn.updateOrder}")
    private String updateOrder;
    @Value("${ghn.previewOrder}")
    private String previewOrder;
    @Value("${ghn.changeCOD}")
    private String changeCOD;
    @Value("${ghn.getOrderDetail}")
    private String getOrderDetail;

}
