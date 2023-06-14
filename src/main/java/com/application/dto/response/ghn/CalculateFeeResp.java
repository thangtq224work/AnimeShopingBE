package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CalculateFeeResp extends BaseAddress{
    @JsonProperty("data")
    private CalculateFeeResp.Fee fee;
    @Data
    public static class Fee{
        private double total;
        private double service_fee;
        private double insurance_fee;
        private double pick_station_fee;
        private double coupon_value;
        private double r2s_fee;
        private double return_again;
        private double document_return;
        private double double_check;
        private double cod_fee;
        private double pick_remote_areas_fee;
        private double deliver_remote_areas_fee;
        private double cod_failed_fee;

    }
}
