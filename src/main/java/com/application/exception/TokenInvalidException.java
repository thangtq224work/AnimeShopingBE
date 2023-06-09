package com.application.exception;

import lombok.Getter;

@Getter
public class TokenInvalidException extends CustomException{
    public TokenInvalidException(String msg){
        super(msg);
    }
}
