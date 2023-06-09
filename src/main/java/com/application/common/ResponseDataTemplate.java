package com.application.common;

import org.springframework.http.HttpStatus;

public class ResponseDataTemplate {
    public static ResponseData.Builder NOT_FOUND = new ResponseData.Builder().code(HttpStatus.NOT_FOUND.value());
    public static ResponseData.Builder BAD_REQUEST = new ResponseData.Builder().code(HttpStatus.BAD_REQUEST.value());
    public static ResponseData.Builder UNAUTHORIZED = new ResponseData.Builder().code(HttpStatus.UNAUTHORIZED.value());
    public static ResponseData.Builder FORBIDDEN = new ResponseData.Builder().code(HttpStatus.FORBIDDEN.value());
    public static ResponseData.Builder OK = new ResponseData.Builder().code(HttpStatus.OK.value());

}
