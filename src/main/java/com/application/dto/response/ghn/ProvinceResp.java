package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProvinceResp extends BaseAddress{
    @JsonProperty("data")
    List<ProvinceResp.Province> data;

    public static class Province {
        @JsonProperty("ProvinceID")
        Integer ProvinceID;
        @JsonProperty("ProvinceName")
        String ProvinceName;
        @JsonProperty("CountryID")
        Integer CountryID;
    }
}
