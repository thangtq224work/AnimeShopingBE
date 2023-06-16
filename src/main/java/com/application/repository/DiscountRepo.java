package com.application.repository;

import com.application.entity.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DiscountRepo extends JpaRepository<Discount,Integer>,JpaSpecificationExecutor<Discount> {
    Discount findByIdAndStatus(Long id, Integer status);
    Discount findByIdAndDiscountType(Long id, Byte discountType);
    @Query("SELECT de FROM Discount de WHERE de.status =:status AND :current BETWEEN de.discountStart AND de.discountEnd")
    public Page<Discount> getDiscountActive(Pageable pageable, @Param("status") Boolean status, @Param("current") Date current);

}
