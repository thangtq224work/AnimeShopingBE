package com.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderReq {
    private Integer id;
    @NotNull
    @Length(min = 5,max = 300)
    private String address;
    @NotNull
    @Pattern(regexp = "/^((09|03)+[0-9]{8})$/g")
    private String phone;
    @Min(0)
    private BigDecimal shippingFee;
    @Min(0)
    @NotNull
    private BigDecimal totalPrice;
    private BigDecimal customerMoney;
    private String ghnCode;
    @Length(min = 5,max = 50)
    private String name;
    @NotNull
    @NotBlank
    private String userId;
    @Length(max = 500)
    private String description;
    @NotBlank
    @NotNull
    private String addressCode;
    @NotNull
    @JsonProperty("product")
    private List<OrderReq.Product> list;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product{
        @NotNull
        private Integer id;
        @NotNull
        @Min(1)
        private Integer quantity;
    }


}
