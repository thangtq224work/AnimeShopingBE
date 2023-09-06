package com.application.utils;

import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;

public class StoreProcedureCaller extends SimpleJdbcCall {

    public StoreProcedureCaller(DataSource dataSource) {
        super(dataSource);
    }
}
