package com.application.repository;

import com.application.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ImageRepo extends JpaRepository<ProductImage, Integer> {

}
