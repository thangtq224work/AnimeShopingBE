package com.application.config.security;

import com.application.common.ResponseData;
import com.application.common.ResponseDataTemplate;
import com.application.service.AccountDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AccountDetailService accountDetailService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        String username = null;
        if(isPermitted(request)){
            filterChain.doFilter(request, response); // chạy filter bên dưới
            return; // ngưng không chay code bên dưới
        }
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response); // chay filter bên dưới
//            return; // ngưng ko chạy code bên dưới
//        }
        if(authHeader == null){
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseDataTemplate.FORBIDDEN.message("Access denied").build()));
            return;
        }
        jwt = authHeader.substring(7);// "Bearer " co 7 ki tu
        try {
            username = jwtService.getUsernameFromJwtToken(jwt,true);
        } catch (Exception e) {
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseDataTemplate.FORBIDDEN.message("Access denied").build()));
            return;
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = accountDetailService.loadUserByUsername(username);
            if (jwtService.isValidToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null,
                        userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        filterChain.doFilter(request, response);
    }
    private boolean isPermitted(HttpServletRequest request) {
        return request.getServletPath().equals("/user/login")
                || request.getServletPath().contains("/api/auth/")
                || request.getServletPath().contains("/api/v1")
                ;
    }
}
