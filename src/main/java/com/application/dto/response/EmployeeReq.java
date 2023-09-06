package com.application.dto.response;

import com.application.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeReq {
    private Integer id;
    @NotBlank
    private String username;
    @NotBlank
    private String fullname;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    private String image;
    @NotNull
    private Date birthday;
    @NotNull
    private Boolean status;
    @NotNull
    private String[] roles;

    public EmployeeReq(Account account){
        this.id = 1;
        this.username = account.getUsername();
        this.fullname = account.getFullName();
        this.password = account.getPassword();
        this.email = account.getEmail();
        this.phone = account.getPhone();
        this.image = account.getImage();
        this.birthday = account.getBirthday();
        this.status = account.getStatus();
        this.roles = getRoleFromAccount(account);
    }
    private String[] getRoleFromAccount(Account account) {
        String scopes[] = account.getAccountRoles().stream().map((accountRole) -> accountRole.getRole().getRole()).toArray(size -> new String[size]);
        return scopes;
    }

}
