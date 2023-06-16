package com.application.repository;


import com.application.entity.ProductDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDiscountRepo extends JpaRepository<ProductDiscount,Integer> {
    //    @Query("SELECT pd FROM ProductDiscountEntity pd WHERE pd.discountEntity.id := id")
//    List<ProductDiscountEntity> getByDiscountId(@Param("id") Long id);
    List<ProductDiscount> findByProductIdAndStatus(Integer Product, boolean status);
    @Query("SELECT pd FROM ProductDiscount pd WHERE pd.discountId =:id")
    List<ProductDiscount> getByDiscountId(@Param("id") Integer id);
    @Query("SELECT count(pd) FROM ProductDiscount pd WHERE pd.discountId =:id AND pd.productId =:pid and  pd.status=1")
    Long getByDiscountIdAndProductId(@Param("id") Integer id,@Param("pid") Integer pid);

}