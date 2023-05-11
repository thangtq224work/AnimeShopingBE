package com.application.repository;

import com.application.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplierRepo extends JpaRepository<Supplier, Integer> {

}
