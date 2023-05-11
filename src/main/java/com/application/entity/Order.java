package com.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String clientName;
    private String address;
    private String phone;
    private Float shippingFee;
    private BigDecimal totalPrice;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

}
