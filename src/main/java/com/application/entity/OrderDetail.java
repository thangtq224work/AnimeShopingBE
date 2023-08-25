package com.application.entity;

import com.application.dto.response.ProductResp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    private BigDecimal originalPrice;
    private BigDecimal sellPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    @Enumerated(EnumType.ORDINAL)
    private StatusOrderDetail status = StatusOrderDetail.SUCCESS;

    @AllArgsConstructor
    @Getter
    public enum StatusOrderDetail {
        CANCEL, SUCCESS

    }
    public OrderDetail(ProductResp resp,Order oid){
        this.quantity = resp.getQuantity();
        this.originalPrice = resp.getPrice();
        this.sellPrice = resp.getPriceSell();
        this.product = new Product(resp.getId());
        this.order = oid;


    }
}
