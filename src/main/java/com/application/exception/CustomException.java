package com.application.exception;

import com.application.constant.Constant;
import lombok.Getter;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

@Getter
public class CustomException extends RuntimeException {
    protected String message;
    protected String at;


    public CustomException(String message) {
        super(message);
        this.message = message;
        this.at = DateFormatUtils.format(new Date(), Constant.DateFormat.FORMAT_DATE);
    }
}
