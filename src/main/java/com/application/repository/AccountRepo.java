package com.application.repository;


import com.application.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, String> {
    @Query("SELECT ac FROM Account ac WHERE ac.username =:us AND ac.status =:st ")
    Optional<Account> findByUsername(@Param("us")String us,@Param("st") Boolean status);
}
