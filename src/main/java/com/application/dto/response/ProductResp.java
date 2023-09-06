package com.application.dto.response;

import com.application.dto.*;
import com.application.entity.Product;
import com.application.entity.Supplier;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal priceSell;
    private Boolean status;
    private CategoryDto category;
    private MaterialDto material;
    private TypeProductDto typeProduct;
    private SupplierDto supplier;
    private List<ImageDto> images;

    public ProductResp(Product product) {
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
        this.quantity = product.getQuantity();
        this.category = new CategoryDto(product.getCategory());
        this.material = new MaterialDto(product.getMaterial());
        this.supplier = new SupplierDto(product.getSupplier());
        this.typeProduct = new TypeProductDto(product.getTypeProduct());
        if (product.getProductImages() != null) {
            this.images = product.getProductImages().stream().map((img) -> {
                img.setUrl(buildUrl(img.getUrl()));
                return new ImageDto(img);
            }).collect(Collectors.toList());
        }
    }
    public ProductResp(Product product,boolean forUser) { // forUser : price 30 priceSell 50 => price 50 priceSell 50
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.status = product.getStatus();
        this.weight = product.getWeight();
        this.height = product.getHeight();
        this.length = product.getLength();
        this.width = product.getWidth();
        if(!forUser){
            this.price = product.getPrice();
        }else{
            this.price = product.getPriceSell();
        }
        this.priceSell = product.getPriceSell();
        this.quantity = product.getQuantity();
        this.category = new CategoryDto(product.getCategory());
        this.material = new MaterialDto(product.getMaterial());
        this.supplier = new SupplierDto(product.getSupplier());
        this.typeProduct = new TypeProductDto(product.getTypeProduct());
        if(!forUser){ // cause update url image in bd
            if (product.getProductImages() != null) {
                this.images = product.getProductImages().stream().map((img) -> {
                    return new ImageDto(img);
                }).collect(Collectors.toList());
            }
        }else{
            if (product.getProductImages() != null) {
                this.images = product.getProductImages().stream().map((img) -> {
                    img.setUrl(buildUrl(img.getUrl()));
                    return new ImageDto(img);
                }).collect(Collectors.toList());
            }
        }
    }

    private String buildUrl(String resource) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentServletMapping();
        builder.pathSegment("api","v1", "show");
        builder.queryParam("url", resource);
        return builder.toUriString();
    }
}
