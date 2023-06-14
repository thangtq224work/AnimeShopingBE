package com.application.exception;

import lombok.Getter;

@Getter
public class ParamInvalidException extends CustomException{
    public ParamInvalidException(String s){
        super(s);
    }
}
