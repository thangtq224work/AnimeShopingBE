package com.application.repository;

import com.application.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CategoryRepo extends JpaRepository<Category, Integer> {
    @Query("SELECT sp FROM Category sp WHERE sp.status=:status")
    List<Category> getCategoryByStatus(@Param("status") Boolean status);
}
