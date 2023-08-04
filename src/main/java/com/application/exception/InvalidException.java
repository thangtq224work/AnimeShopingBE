package com.application.exception;

import lombok.Getter;

@Getter
public class InvalidException extends CustomException{
    public InvalidException(String msg){
        super(msg);
    }
}
