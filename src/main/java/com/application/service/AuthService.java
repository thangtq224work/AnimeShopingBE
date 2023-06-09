package com.application.service;

import com.application.config.security.JwtService;
import com.application.constant.Constant;
import com.application.dto.request.LoginReq;
import com.application.dto.request.TokenReq;
import com.application.dto.response.LoginResp;
import com.application.entity.Account;
import com.application.entity.RefreshToken;
import com.application.exception.NotFoundException;
import com.application.exception.TokenInvalidException;
import com.application.exception.UsernameOrPasswordNotValidException;
import com.application.repository.AccountRepo;
import com.application.repository.RefreshTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private RefreshTokenRepo refreshTokenRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtService jwtService;

    public LoginResp login(LoginReq loginReq) {
        System.out.println(encoder.encode(loginReq.getPassword()));
        Account account = accountRepo.findByUsername(loginReq.getUsername(), Constant.Status.ACTIVE).orElseThrow(() -> new UsernameOrPasswordNotValidException("Username or password invalid"));
        if (!encoder.matches(loginReq.getPassword(), account.getPassword())) {
            throw new UsernameOrPasswordNotValidException("Username or password invalid");
        }
        String scopes[] = getRoleFromAccount(account);
        String access_token = jwtService.generateToken(account, scopes);
        String refresh_token = jwtService.generateRefreshToken(account.getUsername());
        LoginResp loginResp = new LoginResp();
        loginResp.setAccess_token(access_token);
        loginResp.setAccess_token_expired(jwtService.extractAllClaims(access_token, true).getExpiration());
        loginResp.setRefresh_token(refresh_token);
        loginResp.setRefresh_token_expired(jwtService.extractAllClaims(refresh_token, false).getExpiration());
        loginResp.setRole(scopes);
        refreshTokenRepo.save(new RefreshToken(null, refresh_token, loginReq.getUsername()));
        return loginResp;
    }

    public LoginResp refresh(TokenReq tokenReq) {
        if (tokenReq.getRefreshToken() == null) {
            throw new TokenInvalidException("Token is invalid or expired");
        }
        Optional<RefreshToken> optional = refreshTokenRepo.findByRefreshToken(tokenReq.getRefreshToken());
        if (optional.isPresent() && jwtService.validateRefreshToken(tokenReq.getRefreshToken())) {
            String username = "";
            try {
                username = jwtService.getUsernameFromJwtToken(tokenReq.getRefreshToken(), false);
            } catch (Exception exception) {
                throw new TokenInvalidException("Token is invalid or expired");
            }
            Account account = accountRepo.findByUsername(username, Constant.Status.ACTIVE).orElseThrow(() -> new NotFoundException("Username not found"));
            String scopes[] = getRoleFromAccount(account);
            LoginResp dto = new LoginResp();
            String access_token = jwtService.generateToken(account, scopes);
            dto.setAccess_token_expired(jwtService.extractAllClaims(access_token, true).getExpiration());
            dto.setAccess_token(access_token);
            return dto;
        } else {
            throw new TokenInvalidException("Token is invalid or expired");
        }
    }

    public Map getUser(TokenReq tokenReq) {
        if (tokenReq.getAccessToken() == null) {
            throw new TokenInvalidException("Token is invalid or expired");
        }
        String username = "";
        Date expired = null;
        try {
            username = jwtService.getUsernameFromJwtToken(tokenReq.getAccessToken(), true);
            expired = jwtService.getExpiryDate(tokenReq.getAccessToken());
        } catch (Exception exception) {
            throw new TokenInvalidException("Token is invalid or expired");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("expired", expired.getTime());
        return map;
    }

    public Map getRole(TokenReq tokenReq) {
        if (tokenReq.getAccessToken() == null) {
            throw new TokenInvalidException("Token is invalid or expired");
        }
        String username = "";
        Date expired = null;

        try {
            username = jwtService.getUsernameFromJwtToken(tokenReq.getAccessToken(), true);
            expired = jwtService.getExpiryDate(tokenReq.getAccessToken());

        } catch (Exception exception) {
            throw new TokenInvalidException("Token is invalid or expired");
        }
        Account account = accountRepo.findByUsername(username, Constant.Status.ACTIVE).orElseThrow(() -> new NotFoundException("Username not found"));
        Map<String, Object> map = new HashMap<>();
        map.put("role", getRoleFromAccount(account));
        map.put("expired", expired.getTime());
        return map;
    }

    private String[] getRoleFromAccount(Account account) {
        String scopes[] = account.getRoles().stream().map((role) -> role.getRole()).toArray(size -> new String[size]);
        return scopes;
    }
}
