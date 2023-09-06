package com.application.service;

import com.application.common.PageData;
import com.application.dto.response.EmployeeReq;
import org.springframework.data.domain.Pageable;

public interface AccountService {
//    PageData<>
    public PageData<EmployeeReq> getAll(String user,String search, Pageable pageable);
    public int newAccount(EmployeeReq employeeReq);
    public int updateAccount(EmployeeReq employeeReq);
}
