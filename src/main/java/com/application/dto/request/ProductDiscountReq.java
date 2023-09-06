package com.application.dto.request;


import com.application.dto.CategoryDto;
import com.application.dto.SupplierDto;
import com.application.entity.Product;
import com.application.entity.ProductDiscount;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDiscountReq {
    private Integer id;
    private Boolean status;
    private ProductDiscountReq.ProductForDiscountDto product;
    private Integer discountId;

    public ProductDiscountReq(ProductDiscount product){
        this.id = product.getId();
        this.status = product.getStatus();
        this.discountId = product.getDiscountId();
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
//    @JsonRootName("product")
    public static class ProductForDiscountDto{
        private Integer id;
        private String figure;
        private String description;
        private CategoryDto categoryId;
        private SupplierDto manufacturerID;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private BigDecimal priceSale;
        private Boolean status;
        public ProductForDiscountDto(Product productEntity){
            this.id=productEntity.getId();
            this.figure=productEntity.getName();
            this.description=productEntity.getDescription();
            this.price=productEntity.getPriceSell();
            this.originalPrice=productEntity.getPrice();
            this.status = productEntity.getStatus();
            this.categoryId = new CategoryDto(productEntity.getCategory());
            this.manufacturerID = new SupplierDto(productEntity.getSupplier());
        }
    }
}
