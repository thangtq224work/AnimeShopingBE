package com.application.repository;

import com.application.entity.AccountRole;
import com.application.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRoleRepo extends JpaRepository<AccountRole,Integer> {

    @Query("SELECT ac FROM AccountRole ac WHERE ac.role.role =:role AND ac.account.username =:id")
    Optional<AccountRole> getRoleByNameAndAccountId(@Param("role")String role,@Param("id")String id);
}
