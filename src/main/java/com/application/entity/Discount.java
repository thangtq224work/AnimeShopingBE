package com.application.entity;


import com.application.dto.request.DiscountReq;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "discount")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Discount extends BaseDiscount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "discount_name")
    private String discountName;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private Boolean status;
    @Column(name = "image")
    private String image;
    public Discount(DiscountReq discountReq){
        this.id = discountReq.getId();
        this.discountName = discountReq.getName();
        this.description = discountReq.getDescription();
        this.status = discountReq.getStatus();
        this.discountAmount = discountReq.getDiscountAmount();
        this.discountType = discountReq.getDiscountType();
        this.discountEnd = discountReq.getDiscountEnd();
        this.discountStart = discountReq.getDiscountStart();

    }
}
