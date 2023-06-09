package com.application.dto.request;

import lombok.Data;

@Data
public class TokenReq {
    private String refreshToken;
    private String accessToken;
}
