package com.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgetPasswordReq {
    @NotBlank
    @NotNull
    String token;
    @NotBlank
    @NotNull
    String newPassword;
    @NotBlank
    @NotNull
    String confirm;
}
