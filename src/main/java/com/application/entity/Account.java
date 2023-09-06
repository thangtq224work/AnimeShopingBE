package com.application.entity;

import com.application.dto.request.RegisterReq;
import com.application.dto.response.EmployeeReq;
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
public class Account extends BaseEntity implements Serializable {
    @Id
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String phone;
    private String image;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    private Boolean status;
    private String token;
    private Date expiredToken;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private List<Order> orders;
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "account")
//    @JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "username"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<AccountRole> accountRoles;
    public Account(RegisterReq registerReq){
        this.username = registerReq.getUsername();
        this.fullName = registerReq.getFullname();
        this.password = registerReq.getPassword();
        this.phone = registerReq.getPhone();
        this.email = registerReq.getEmail();
    }
    public Account (EmployeeReq employeeReq){
        this.username = employeeReq.getUsername();
        this.fullName = employeeReq.getFullname();
        this.phone = employeeReq.getPhone();
        this.email = employeeReq.getEmail();
        this.status = employeeReq.getStatus();
        this.birthday = employeeReq.getBirthday();

    }
    public void convert(EmployeeReq employeeReq){
        this.fullName = employeeReq.getFullname();
        this.phone = employeeReq.getPhone();
        this.email = employeeReq.getEmail();
        this.status = employeeReq.getStatus();
        this.birthday = employeeReq.getBirthday();
    }
}
