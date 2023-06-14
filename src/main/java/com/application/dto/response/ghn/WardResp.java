package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WardResp extends BaseAddress {
    @JsonProperty("data")
    List<WardResp.Ward> data;
    public static class Ward{
        @JsonProperty("WardCode")
        Integer WardCode;
        @JsonProperty("WardName")
        String WardName;
        @JsonProperty("DistrictID")
        Integer DistrictID;
    }
}
