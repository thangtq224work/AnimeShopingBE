package com.application.common;

import org.springframework.http.HttpStatus;

public class ErrorResponseTemplate {
    public static ErrorResponse NOT_FOUND = new ErrorResponse(HttpStatus.NOT_FOUND.value(),"",null);

}
