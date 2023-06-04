package com.application.dto.request;

import com.application.dto.ImageDto;
import com.application.entity.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductReq {
    private Integer id;
    @NotNull
    @Length(min = 3, max = 100)
    private String name;
    @NotNull
    @Length(min = 0, max = 1000)
    private String description;
    @NotNull
    @Min(value = 1)
    private Float weight;
    @NotNull
    @Min(value = 1)
    private Float width;
    @NotNull
    @Min(value = 1)
    private Float height;
    @NotNull
    @Min(value = 1)
    private Float length;
    @NotNull
    private Integer quantity;
    @NotNull
    @Min(value = 1)
    private BigDecimal price;
    @NotNull
    @Min(value = 1)
    private BigDecimal priceSell;
    @NotNull
    private Boolean status;
    @NotNull
    private Integer category;
    @NotNull
    private Integer material;
    @NotNull
    @JsonAlias("type")
    private Integer typeProduct;
    @NotNull
    private Integer supplier;
    private List<ImageDto> images;

    public ProductReq(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.weight = product.getWeight();
        this.width = product.getWidth();
        this.height = product.getHeight();
        this.length = product.getLength();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.priceSell = product.getPriceSell();
        this.material = product.getMaterial().getId();
        this.category = product.getCategory().getId();
        this.supplier = product.getSupplier().getId();
        this.typeProduct = product.getTypeProduct().getId();
    }
}