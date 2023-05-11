package com.application.repository;

import com.application.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MaterialRepo extends JpaRepository<Material, Integer> {

}
