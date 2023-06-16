package com.application.dto.request;

import com.application.dto.CategoryDto;
import com.application.dto.SupplierDto;
import com.application.entity.Product;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonRootName("product")
public class ProductForDiscountReq {
    private Integer id;
    private String figure;
    private String description;
    private CategoryDto categoryId;
    private SupplierDto manufacturerID;
    private BigDecimal price;
    private BigDecimal priceSale;
    private Boolean status;

    public ProductForDiscountReq(Product productEntity) {
        this.id = productEntity.getId();
        this.figure = productEntity.getName();
        this.description = productEntity.getDescription();
        this.price = productEntity.getPriceSell();
        this.status = productEntity.getStatus();
    }
}
