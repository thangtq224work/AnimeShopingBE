package com.application.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterReq {
    @NotBlank
    @Length(min = 5,max = 30)
    private String username;
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 2,max = 15)
    private String phone;
    @NotBlank
    @Length(min = 5,max = 30)
    private String password;
    @NotBlank
    @Length(min = 5,max = 50)
    private String fullname;
    private String url;
}
