package com.application.repository;

import com.application.entity.TypeProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeProductRepo extends JpaRepository<TypeProduct, Integer> {
}
