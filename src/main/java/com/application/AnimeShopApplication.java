package com.application;

import com.application.common.Store;
import com.application.constant.Constant;
import com.application.entity.Account;
import com.application.entity.Role;
import com.application.repository.AccountRepo;
import com.application.repository.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@SpringBootApplication()
@EnableCaching
public class AnimeShopApplication implements CommandLineRunner {
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    Store store;

    public static void main(String[] args) {
        SpringApplication.run(AnimeShopApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // init role
        List<String> roles = Constant.AccountRole.getRoles();
        for (String role: roles) {
            Optional<Role> r = roleRepo.getRole(role);
            if(r.isEmpty()){
                roleRepo.save(new Role(null,role));
            }
        }
//        init account
        Optional<Account> optional = accountRepo.findByUsername("admin",Constant.Status.ACTIVE);
        if(optional.isEmpty()){
            Account account = new Account();
            account.setUsername("admin");
            account.setPassword(encoder.encode("admin123"));
            account.setStatus(Constant.Status.ACTIVE);
            account.setRoles(List.of(roleRepo.getRole(Constant.AccountRole.ADMIN).get()));
            accountRepo.save(account);
        }
        store.init();
    }
}
