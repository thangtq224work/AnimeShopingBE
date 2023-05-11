package com.application.dto;

import com.application.constant.Constant;
import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

@Data
public class Response {
    String at;
    Object data;

    public Response(Object data) {
        this.data = data;
        this.at = DateFormatUtils.format(new Date(), Constant.DateFormat.FORMAT_DATE);
    }

}
