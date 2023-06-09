package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.LoginReq;
import com.application.dto.request.TokenReq;
import com.application.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
