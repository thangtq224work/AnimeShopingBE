package com.application.exception;

import lombok.Getter;

@Getter
public class UsernameOrPasswordNotValidException extends CustomException{
    public UsernameOrPasswordNotValidException(String message){
        super(message);
    }
}
