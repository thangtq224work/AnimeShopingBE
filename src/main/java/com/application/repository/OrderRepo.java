package com.application.repository;

import com.application.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Integer> , JpaSpecificationExecutor<Order> {
    @Query("SELECT ors FROM Order ors WHERE ors.account.username =:us")
    public List<Order> getByUsername(@Param("us")String user);
    @Query("SELECT ors FROM Order ors WHERE ors.account.username =:us AND ors.id =:id")
    public Optional<Order> getByIdAndUsername(@Param("us")String user, @Param("id") Integer id);
    @Query("SELECT ors FROM Order ors WHERE ors.paymentCode =:id")
    public Optional<Order> getByPaymentCode(@Param("id") String oid);
}
