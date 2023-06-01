package com.application.repository;

import com.application.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MaterialRepo extends JpaRepository<Material, Integer> {

    @Query("SELECT sp FROM Material sp WHERE sp.status=:status")
    List<Material> getMaterialByStatus(@Param("status") Boolean status);
}
