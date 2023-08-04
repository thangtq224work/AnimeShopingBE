package com.application.config;

import com.application.dto.response.ghn.StoreResp;
import com.application.inject.GhnBean;
import com.application.inject.VnPay;
import com.application.service.GhnService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BeanConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.set
        return objectMapper;
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public GhnBean ghnBean() {
        return new GhnBean();
    }
    @Bean
    public VnPay vnPay() {
        return new VnPay();
    }
    @Bean
    public StoreResp.StoreGhn getStoreGhn(){
        return new StoreResp.StoreGhn();
    }
}
