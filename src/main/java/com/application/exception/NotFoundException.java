package com.application.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends CustomException {

    public NotFoundException(String message) {
        super(message);
    }
}
