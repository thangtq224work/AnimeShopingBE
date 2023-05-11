package com.application.dto;

import com.application.entity.Product;
import com.application.entity.Supplier;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResp {
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
    private CategoryDto category;
    private MaterialDto material;
    private TypeProductDto typeProduct;
    private SupplierDto supplier;
    public ProductResp(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.weight = product.getWeight();
        this.height = product.getHeight();
        this.length = product.getLength();
        this.width = product.getWidth();
        this.price = product.getPrice();
        this.priceSell = product.getPriceSell();
        this.category = new CategoryDto(product.getCategory());
        this.material = new MaterialDto(product.getMaterial());
        this.supplier = new SupplierDto(product.getSupplier());
        this.typeProduct = new TypeProductDto(product.getTypeProduct());

    }
}
