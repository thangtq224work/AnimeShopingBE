package com.application.service;

import com.application.config.FontEndConfig;
import com.application.config.MailService;
import com.application.config.RegisterForThymeleaf;
import com.application.config.security.JwtService;
import com.application.constant.Constant;
import com.application.dto.request.*;
import com.application.dto.response.LoginResp;
import com.application.entity.Account;
import com.application.entity.AccountRole;
import com.application.entity.RefreshToken;
import com.application.exception.*;
import com.application.repository.AccountRepo;
import com.application.repository.RefreshTokenRepo;
import com.application.repository.RoleRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private MailService mailService;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private FontEndConfig fontEndConfig;
    private final int expiredMinute=15;


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
        loginResp.setUsername(account.getUsername());
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
    public int register(RegisterReq registerReq) throws MessagingException {
        List<Account> accounts = accountRepo.findByUsernameOrEmail(registerReq.getUsername(), registerReq.getEmail());
        if(!accounts.isEmpty()){
            boolean check = false;
            for (Account account : accounts){
                if(account.getStatus() == Constant.AccountStatus.NON_ACTIVE){
                    check = true;
                    accountRepo.deleteById(account.getUsername());
                }
            }
            if(!check){
                throw new EntityAlreadyExistsException("Username or email is already used");
            }
        }
        Account accountEntity = new Account(registerReq);
        accountEntity.setPassword(encoder.encode(registerReq.getPassword()));
        accountEntity.setToken(System.currentTimeMillis()+"_"+ UUID.randomUUID().toString());
        accountEntity.setExpiredToken(new Date(System.currentTimeMillis()+expiredMinute*60*1000));
        accountEntity.setStatus(Constant.AccountStatus.NON_ACTIVE);
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromHttpUrl(registerReq.getUrl());
//        builder.scheme("https");
//        builder.pathSegment(accountEntity.getToken());
        builder.queryParam("token",accountEntity.getToken());
        URI newUri = builder.build().toUri();

        String body ="<p>Xin chào bạn .Bạn đã đăng kí tài khoản của website Japan . Vui lòng "
                + "<a href=\""+newUri.toString()+"\">XÁC THỰC</a> "
                + "tài khoản qua đường dẫn</p>. Vui lòng xác thực email này trước "+new SimpleDateFormat(Constant.DateFormat.FORMAT_DATE).format(accountEntity.getExpiredToken());
        Map<String,Object> map = new HashMap<>();
        System.out.println(newUri.toString());
        map.put("name",accountEntity.getFullName());
        map.put("url",newUri.toString());
        mailService.queue(accountEntity.getEmail(), "Xác thực tài khoản", body,new RegisterForThymeleaf(map));
        accountRepo.save(accountEntity);
        return 1;
    }
    public int registerConfirm(String token) {
        Account accountEntity =  accountRepo.findByToken(token).orElseThrow(()->new InvalidException("Token is invalid"));
        if(new Date().after(accountEntity.getExpiredToken()) || accountEntity.getStatus() != Constant.AccountStatus.NON_ACTIVE){
            throw new InvalidException(("Token is expired. Try again"));
        }
        else{
            accountEntity.setToken("");
            accountEntity.setExpiredToken(null);
            accountEntity.setStatus(Constant.AccountStatus.ACTIVE);
            accountEntity.getAccountRoles().add(new AccountRole(roleRepo.getRole(Constant.AccountRole.CLIENT).get()));
            accountRepo.save(accountEntity);

//            ac.save(new AccountRoleEntity(accountEntity.getUserName(),Constant.AccountRole.CLIENT_ID));

        }

        return 1;
    }
    public void changePassword(ChangePasswordReq changePasswordReq) {
        if(changePasswordReq.getPassword().equals(changePasswordReq.getRepassword()) == false){
            throw new InvalidException("new password not match");
        }
        Account accountEntity = accountRepo.findByUsername(changePasswordReq.getUsername()).orElseThrow(()-> new NotFoundException("Username or password not exactly"));
        if(encoder.matches(changePasswordReq.getOldPassword(),accountEntity.getPassword())== false){
            throw new InvalidException("Username or password not exactly");
        }
        accountEntity.setPassword(encoder.encode(changePasswordReq.getPassword()));
        accountRepo.save(accountEntity);
    }
    public void forgetPassword(ForgetDataReq data) throws MessagingException {
        Account accountEntity = accountRepo.findByEmail(data.getEmail()).orElseThrow(()-> new NotFoundException("Email not found"));
        if(accountEntity.getStatus() == Constant.AccountStatus.NON_ACTIVE){
            throw new InvalidException("Account not exists");
        }
        accountEntity.setToken(System.currentTimeMillis()+"_"+ UUID.randomUUID().toString());
        accountEntity.setExpiredToken(new Date(System.currentTimeMillis()+expiredMinute*60*1000));
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromHttpUrl(data.getUrl());
//        builder.scheme("https");
//        builder.pathSegment(accountEntity.getToken());
//        builder.pathSegment("forget-password","confirm");
        builder.queryParam("token",accountEntity.getToken());
        URI newUri = builder.build().toUri();
        System.out.println(newUri.toString());
        Map<String,Object> map = new HashMap<>();
        map.put("name",accountEntity.getUsername());
        map.put("url",newUri.toString());
        RegisterForThymeleaf forgetPassword = new RegisterForThymeleaf(map);
        forgetPassword.setPath("forget_password");
        mailService.queue(accountEntity.getEmail(), "Quên mật khẩu", "body",forgetPassword);
        accountRepo.save(accountEntity);
    }

    public void confirm(ForgetPasswordReq forgetPasswordto) throws MessagingException {
        if(forgetPasswordto.getNewPassword().equals(forgetPasswordto.getConfirm())== false){
            throw new InvalidException("new password not match");
        }
        Account accountEntity = accountRepo.findByToken(forgetPasswordto.getToken()).orElseThrow(()->new InvalidException("Token is invalid"));
        if(new Date().after(accountEntity.getExpiredToken()) || accountEntity.getStatus() == Constant.AccountStatus.NON_ACTIVE){
            throw new InvalidException(("Token is expired. Try again"));
        }
        accountEntity.setPassword(encoder.encode(forgetPasswordto.getNewPassword()));
        accountEntity.setExpiredToken(null);
        accountEntity.setToken(null);
        accountRepo.save(accountEntity);
    }

    private String[] getRoleFromAccount(Account account) {
        String scopes[] = account.getAccountRoles().stream().map((accountRole) -> accountRole.getRole().getRole()).toArray(size -> new String[size]);
        return scopes;
    }
}
