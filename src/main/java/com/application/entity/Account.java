package com.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {
    @Id
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String phone;
    private String image;
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;
    private Integer status;
    private String token;
    private Date expiredToken;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Order> orders;

}
