package com.application.repository;

import com.application.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepo extends JpaRepository<Order, Integer> , JpaSpecificationExecutor<Order> {
}
