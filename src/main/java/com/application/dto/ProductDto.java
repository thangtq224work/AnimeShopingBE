package com.application.dto;

import com.application.entity.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private Float weight;
    private Float width;
    private Float height;
    private Float length;
    private BigDecimal price;
    private BigDecimal priceSell;
    private Boolean status;
    private Integer category;
    private Integer material;
    @JsonAlias("type")
    private Integer typeProduct;
    private Integer supplier;

    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.weight = product.getWeight();
        this.width = product.getWidth();
        this.height = product.getHeight();
        this.length = product.getLength();
        this.price = product.getPrice();
        this.priceSell = product.getPriceSell();
        this.material = product.getMaterial().getId();
        this.category = product.getCategory().getId();
        this.supplier = product.getSupplier().getId();
        this.typeProduct = product.getTypeProduct().getId();
    }
}