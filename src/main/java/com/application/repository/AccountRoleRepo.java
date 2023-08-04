package com.application.repository;

import com.application.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRoleRepo extends JpaRepository<AccountRole,Integer> {
}
