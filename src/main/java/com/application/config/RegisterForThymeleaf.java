package com.application.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class RegisterForThymeleaf {
    String path = "register";
    Map<String,Object> variable;
    public RegisterForThymeleaf(Map<String,Object> map){
        this.variable = map;
    }
}
