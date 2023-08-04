package com.application.service;

import com.application.constant.Constant;
import com.application.entity.Account;
import com.application.repository.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
@Service
public class AccountDetailService implements UserDetailsService {
    @Autowired
    private AccountRepo accountRepo;
    private final String prefix = "ROLE_";
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepo.findByUsername(username, Constant.Status.ACTIVE)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found ", username)));
        return new User(account.getUsername(),account.getPassword(),mapRoleToAuthority(account));
    }
    private Collection<GrantedAuthority> mapRoleToAuthority(Account accounts) {
        return accounts.getAccountRoles().stream().map(role -> new SimpleGrantedAuthority(prefix+role.getRole().getRole())).collect(Collectors.toList());
    }
}
