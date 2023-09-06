package com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordReq {
    @NotBlank
    @NotNull
    private String username;
    @NotBlank
    @NotNull
    private String oldPassword;
    @NotBlank
    @NotNull
    private String password;
    @NotBlank
    @NotNull
    private String repassword;
}
