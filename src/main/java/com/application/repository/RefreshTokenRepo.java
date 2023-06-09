package com.application.repository;

import com.application.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Long> {
    @Query("SELECT rte FROM RefreshToken rte WHERE rte.refreshToken =:rt")
    public Optional<RefreshToken> findByRefreshToken(@Param("rt") String refreshToken);
}
