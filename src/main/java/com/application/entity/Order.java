package com.application.entity;

import com.application.dto.request.OrderReq;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String clientName;
    private String address;
    private String phone;
    @Column(unique = true)
    private String orderCode;
    @Column(unique = true)
    private String paymentCode;
    private String ghnCode;
    private BigDecimal serviceFee;
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;
    private BigDecimal customerMoney;
    private String description;
    private String addressCode;
    private Integer status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredPayment;
    @Column(length = 700)
    private String urlPayment;
    private String transactionNo;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
    public Order(OrderReq req,Account account,BigDecimal totalPrice){
        this.id= req.getId();;
        this.address = req.getAddress();
        this.phone = req.getPhone();
        this.description = req.getDescription();
        this.addressCode = req.getAddressCode();
        this.shippingFee = req.getShippingFee();
        this.customerMoney = req.getCustomerMoney();
        this.totalPrice = totalPrice;
        this.clientName = req.getName();
        this.account = account;
        this.ghnCode = req.getGhnCode();
    }

}
