package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.response.EmployeeReq;
import com.application.entity.Account;
import com.application.entity.AccountRole;
import com.application.entity.Role;
import com.application.exception.EntityAlreadyExistsException;
import com.application.exception.InvalidException;
import com.application.exception.NotFoundException;
import com.application.repository.AccountRepo;
import com.application.repository.AccountRoleRepo;
import com.application.repository.RoleRepo;
import com.application.service.AccountService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private AccountRoleRepo accountRoleRepo;
    @Autowired
    private PasswordEncoder encoder;

    public PageData<EmployeeReq> getAll(String user,String search, Pageable pageable){
        Specification<Account> specification = (root, query, criteriaBuilder) -> {
            Predicate namePredicate = buildExpLike(search) == null? criteriaBuilder.and():criteriaBuilder.like(root.get("fullName"),buildExpLike(search));
            Predicate userPredicate = criteriaBuilder.notEqual(root.get("username"), user);
            Predicate adminPredicate = criteriaBuilder.notEqual(root.join("accountRoles", JoinType.INNER).join("role",JoinType.INNER).get("role"), Constant.AccountRole.CLIENT);
            return criteriaBuilder.and(namePredicate,userPredicate,adminPredicate);
        };
        Page<Account> page = accountRepo.findAll(specification,pageable);
        List<EmployeeReq> list = page.toList().stream().map(i->new EmployeeReq(i)).collect(Collectors.toList());
        return PageData.of(page,list);
    }

    @Override
    public int newAccount(EmployeeReq employeeReq) {
        List<Account> list = accountRepo.findByUsernameOrEmail(employeeReq.getUsername(), employeeReq.getEmail());
        if(!list.isEmpty()){
            boolean check = false;
            for (Account account : list){
                if(account.getStatus() == Constant.AccountStatus.NON_ACTIVE){
                    check = true;
                    accountRepo.deleteById(account.getUsername());
                }
            }
            if(!check){
                throw new EntityAlreadyExistsException("Username or email is already used");
            }
        }
        if(employeeReq.getRoles().length == 0){
            log.error("Roles must not be empty");
            throw new InvalidException("Param invalid");
        }
        Account account = new Account(employeeReq);
        account.setPassword(encoder.encode(employeeReq.getPassword()));
        account =  accountRepo.save(account);
        List accountRole = new ArrayList();
        for (String item: employeeReq.getRoles()) {
            Optional<Role> optional = roleRepo.getRole(item);
            if(optional.isPresent()){
                accountRole.add(new AccountRole(account,optional.get()));
            }
        }
        accountRoleRepo.saveAll(accountRole);
        return 1;
    }

    @Override
    public int updateAccount(EmployeeReq employeeReq) {
        Optional<Account> optional = accountRepo.findByEmail(employeeReq.getEmail());
        if(!optional.isEmpty() && !optional.get().getUsername().equals(employeeReq.getUsername())){
            throw new EntityAlreadyExistsException("Username or email is already used");
        }
        if(employeeReq.getRoles().length == 0){
            log.error("Roles must not be empty");
            throw new InvalidException("Param invalid");
        }
        Account account = accountRepo.findByUsername(employeeReq.getUsername()).orElseThrow(()->new NotFoundException(""));
        account.convert(employeeReq);
        account = accountRepo.save(account);
        // handler role
        accountRoleRepo.deleteAll(account.getAccountRoles());
        List accountRole = new ArrayList();
        for (String item: employeeReq.getRoles()) {
            Optional<Role> op = roleRepo.getRole(item);
            if(op.isPresent()){
                accountRole.add(new AccountRole(account,op.get()));
            }
        }
        accountRoleRepo.saveAll(accountRole);
        return 0;
    }

    private String buildExpLike(String search){
        if(search == null || search.isEmpty()){
            return  null;
        }
        return  "%"+search+"%";
    }
}
