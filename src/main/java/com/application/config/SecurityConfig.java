package com.application.config;

import com.application.config.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
//        prePostEnabled = true,
//        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        and().cors()
//        http.csrf().and().cors().disable()
//                .authorizeHttpRequests()
//        http.headers().frameOptions().disable().and().cors();
        // phải đặt cors trươc csrf
        http.cors().and().csrf().disable().authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/v1/**").permitAll()
//                .requestMatchers("/api/v1/cart/**", "/api/test-vnpay").permitAll()
//                .requestMatchers("/api/success-payment/**", "/api/test-vnpay-return","/api/v1/bill/**","/api/v1/bill/discount-bill", "/api/v1/bill/export/**").permitAll()
//                .requestMatchers("/api/vnpay/**", "/api/create-pay", "/api/v1/bill/find-address","/api/v1/bill/applyVoucher", "/api/v1/bill/save").permitAll()
//                .requestMatchers("/api/rate/show", "/api/v1/bill/username", "/api/v1/bill/xac_nhan", "/api/v1/bill/update-refund").permitAll()
//                .requestMatchers("/api/products/getAll", "/api/products/show", "/api/products/upload", "/api/products/title", "/api/products/detail","/api/manufacturer","/api/categories").permitAll()
//                .requestMatchers("/api/auth/**","/api/products/**").permitAll()
//                .requestMatchers("/api/rate/show","/api/product-rate/start","/api/rate/get-by-productid").permitAll()
//                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api/v1/cart/**", "api/categories/**", "/api/success-payment/**").permitAll()
                .anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
