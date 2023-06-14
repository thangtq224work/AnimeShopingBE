package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DistrictResp extends BaseAddress {
    @JsonProperty("data")
    List<DistrictResp.District> data;
    public static class District{
        @JsonProperty("DistrictID")
        Integer DistrictID;
        @JsonProperty("DistrictName")
        String DistrictName;
        @JsonProperty("ProvinceID")
        Integer ProvinceID;
    }
}
