package com.application.repository;

import com.application.entity.HotProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotProductRepo extends JpaRepository<HotProduct, Integer> {
}
