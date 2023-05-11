package com.application.repository;

import com.application.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepo extends JpaRepository<Category, Integer> {
}
