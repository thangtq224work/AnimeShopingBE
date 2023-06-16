package com.application.entity;

import com.application.dto.request.ProductDiscountReq;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "product_id")
    private Integer productId;
    @Column(name = "discount_id")
    private Integer discountId;
    public ProductDiscount(ProductDiscountReq productDiscountDto){
        this.id = productDiscountDto.getId();
        this.status = productDiscountDto.getStatus();
        this.productId = productDiscountDto.getProduct().getId();
        this.discountId = productDiscountDto.getDiscountId();
    }
}
