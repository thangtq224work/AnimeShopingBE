package com.application.repository;


import com.application.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {
    @Query("SELECT ac FROM Account ac WHERE ac.username =:us AND ac.status =:st ")
    Optional<Account> findByUsername(@Param("us")String us,@Param("st") Boolean status);
    @Query("SELECT ae FROM Account ae WHERE ae.username =:username")
    public Optional<Account> findByUsername(@Param("username") String username);
    @Query("SELECT ae FROM Account ae WHERE ae.username =:username OR ae.email =:email")
    public List<Account> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    @Query("SELECT ae FROM Account ae WHERE ( ae.username =:username OR ae.email =:email ) AND ae.status =:status")
    public List<Account> findByUsernameOrEmail(@Param("username") String username, @Param("email") String email,@Param("status") Boolean status);
    @Query("SELECT ae FROM Account ae WHERE ae.email =:email")
    public Optional<Account> findByEmail(@Param("email") String email);
    @Query("SELECT ae FROM Account ae WHERE ae.token =:token")
    public Optional<Account> findByToken(@Param("token") String token);
}
