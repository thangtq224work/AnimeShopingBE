package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PreviewResp extends BaseAddress{
    @JsonProperty("data")
    private PreviewResp.Data data;

    @lombok.Data
    public static class Data{
        @JsonProperty("order_code")
        private String orderCode;
        @JsonProperty("fee")
        private PreviewResp.Fee fee;
        @JsonProperty("total_fee")
        private double total;
        @JsonProperty("expected_delivery_time")
        private Date expectedTime;
    }
    @lombok.Data
    public static class Fee{
        @JsonProperty("main_service")
        private double mainService;
        @JsonProperty("insurance")
        private double insurance;
        @JsonProperty("station_do")
        private double stationDo;
        @JsonProperty("station_pu")
        private double stationPu;
        @JsonProperty("return")
        private double returns;
        @JsonProperty("r2s")
        private double r2s;
        @JsonProperty("coupon")
        private double coupon;

    }
}
