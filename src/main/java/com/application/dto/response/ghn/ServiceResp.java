package com.application.dto.response.ghn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ServiceResp extends BaseAddress{
    @JsonProperty("data")
    private List<ServiceResp.Service> services;
    @Data
    public static class Service{
        @JsonProperty("service_id")
        private Integer serviceId;
        @JsonProperty("short_name")
        private String shortName;
        @JsonProperty("service_type_id")
        private Integer serviceTypeId;

    }
}
