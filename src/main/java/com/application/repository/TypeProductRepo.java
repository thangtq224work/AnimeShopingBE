package com.application.repository;

import com.application.entity.TypeProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TypeProductRepo extends JpaRepository<TypeProduct, Integer> {

    @Query("SELECT sp FROM TypeProduct sp WHERE sp.status=:status")
    List<TypeProduct> getTypeProductByStatus(@Param("status") Boolean status);
}
