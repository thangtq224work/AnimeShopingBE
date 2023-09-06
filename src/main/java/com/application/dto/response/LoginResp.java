package com.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResp {
    String username;
    String access_token;
    String refresh_token;
    Date refresh_token_expired;
    Date access_token_expired;
    String[] role;
}
