package com.application.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "account_role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne()
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne()
    @JoinColumn(name = "account_id",referencedColumnName = "username")
    private Account account;
    public AccountRole(Account account,Role role){
        this.account = account;
        this.role = role;
    }
    public AccountRole(Role role){
        this.account = account;
        this.role = role;
    }

}
