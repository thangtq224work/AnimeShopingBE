package com.application.exception.handler;

import com.application.common.ErrorResponse;
import com.application.common.ErrorResponseTemplate;
import com.application.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandlerException {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(NotFoundException exception) {
        return new ErrorResponse.Builder().builderFromObject(ErrorResponseTemplate.NOT_FOUND).message(exception.getMessage()).build();
    }
}
