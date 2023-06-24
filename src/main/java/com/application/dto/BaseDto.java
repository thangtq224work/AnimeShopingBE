package com.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDto {
    protected Date createAt;
    protected Date updateAt;
    protected String updateBy;
    protected String createBy;
}
