package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StoreResp extends BaseAddress{
    @JsonProperty("data")
    private StoreResp.Store store;
    @Data
    public static class Store{
        @JsonProperty("last_offset")
        private Integer lastOffset;
        @JsonProperty("shops")
        private List<StoreResp.StoreGhn> stores;
    }

    @Data
    public static class StoreGhn {
        @JsonProperty("_id")
        private Integer id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("address")
        private String address;
        @JsonProperty("ward_code")
        private String wardCode;
        @JsonProperty("district_id")
        private Integer districtId;
        @JsonProperty("client_id")
        private Integer clientId;
        @JsonProperty("bank_account_id")
        private Integer bankAccountId;
        @JsonProperty("status")
        private Byte status;
    }
}

