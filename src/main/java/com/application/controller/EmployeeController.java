package com.application.controller;


import com.application.common.ResponseDataTemplate;
import com.application.dto.response.EmployeeReq;
import com.application.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin("*")
public class EmployeeController {
    @Autowired
    private AccountService accountService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(Authentication authentication,
                                    @RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                    @RequestParam(name = "size",required = false,defaultValue = "6") Integer size,
                                    @RequestParam(name = "search",required = false,defaultValue = "") String search
    ){
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page-1, size, sort);
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(accountService.getAll(authentication.getName(),search,pageable)).build(), HttpStatus.OK);
    }
    @PostMapping("/new")
    public ResponseEntity<?> newAccount(@RequestBody EmployeeReq employeeReq){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(accountService.newAccount(employeeReq)).build(), HttpStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateAccount(@RequestBody EmployeeReq employeeReq){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(accountService.updateAccount(employeeReq)).build(), HttpStatus.OK);
    }
}
