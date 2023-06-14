package com.application.inject;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
@ToString
public class JwtConstant {
//    @Value("${secret}")
    private String jwtSecret;
//    @Value("${expiration}")
    private Long jwtExpiration;
    private Refresh refresh;
    @Data
    @ConfigurationProperties(prefix = "refresh")
    public static class Refresh{
        private String jwtSecret;
        private Long jwtExpiration;
    }
}
