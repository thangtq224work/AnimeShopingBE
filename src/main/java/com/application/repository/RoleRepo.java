package com.application.repository;

import com.application.entity.Account;
import com.application.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Integer> {
    @Query("SELECT ac FROM Role ac WHERE ac.role =:role")
    Optional<Role> getRole(@Param("role")String us);
}
