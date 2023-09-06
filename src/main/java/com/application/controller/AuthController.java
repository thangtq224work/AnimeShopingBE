package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.*;
import com.application.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq login) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(authService.login(login)).build(), HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody TokenReq req) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(authService.refresh(req)).build(), HttpStatus.OK);
    }
    @PostMapping("/get-user")
    public ResponseEntity<?> getUser(@RequestBody TokenReq req) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(authService.getUser(req)).build(), HttpStatus.OK);
    }
    @PostMapping("/get-role")
    public ResponseEntity<?> getRole(@RequestBody TokenReq req) {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(authService.getRole(req)).build(), HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterReq registerReq) throws MessagingException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(authService.register(registerReq)).build(), HttpStatus.OK);
    }
    @GetMapping("/register/confirm")
    public ResponseEntity<?> registerConfirm(@RequestParam(name = "token") String token) throws MessagingException {
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(authService.registerConfirm(token)).build(), HttpStatus.OK);
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordReq passwordDto) {
        authService.changePassword(passwordDto);
        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }
    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody() ForgetDataReq forgetDataReq) throws MessagingException {
        authService.forgetPassword(forgetDataReq);
        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }
    @PostMapping("/confirm-password")
    public ResponseEntity<?> confirm(@Valid @RequestBody ForgetPasswordReq forgetPasswordto) throws MessagingException {
        authService.confirm(forgetPasswordto);
        return new ResponseEntity<>(ResponseDataTemplate.OK.build(), HttpStatus.OK);
    }

}
