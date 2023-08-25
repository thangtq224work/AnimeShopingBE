package com.application.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends CustomException{
    public EntityAlreadyExistsException(String message){
        super(message);
    }
}
