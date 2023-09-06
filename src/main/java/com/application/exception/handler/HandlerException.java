package com.application.exception.handler;

import com.application.common.ResponseData;
import com.application.common.ResponseDataTemplate;
import com.application.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class HandlerException {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerNotFoundException(NotFoundException exception) {
        return ResponseDataTemplate.NOT_FOUND.message(exception.getMessage()).build();
    }
    @ExceptionHandler(UsernameOrPasswordNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerUsernameOrPasswordNotValidException(UsernameOrPasswordNotValidException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerTokenInvalidException(TokenInvalidException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(ParamInvalidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerParamInvalidException(ParamInvalidException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(InvalidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerInvalidException(InvalidException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerEntityAlreadyExistsException(EntityAlreadyExistsException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage()).build();
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        return new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value()).message("param invalid").build();
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData handlerException(Exception exception) {
        log.error(exception.getMessage());
        exception.printStackTrace();
        return new ResponseData.Builder().code(99).message("Server is processing").build();
    }
}
