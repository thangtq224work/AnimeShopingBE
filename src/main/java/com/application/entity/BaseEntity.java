package com.application.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createAt;
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    protected Date updateAt;
    @LastModifiedBy
    protected String updateBy;
    @CreatedBy
    @Column(updatable = false)
    protected String createBy;


}
