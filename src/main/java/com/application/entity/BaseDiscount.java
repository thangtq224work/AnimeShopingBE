package com.application.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@ToString
public class BaseDiscount extends BaseEntity implements Serializable {
    @Column(name = "discount_start")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date discountStart;
    @Column(name = "discount_end")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date discountEnd;
    @Column(name = "discount_type")
    protected Byte discountType;
    @Column(name = "discount_amount")
    protected BigDecimal discountAmount;
}
