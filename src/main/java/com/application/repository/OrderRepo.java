package com.application.repository;

import com.application.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Integer> , JpaSpecificationExecutor<Order> {
    @Query("SELECT ors FROM Order ors WHERE ors.account.username =:us")
    public Page<Order> getByUsername(@Param("us")String user, Pageable pageable);
    @Query("SELECT ors FROM Order ors WHERE ors.account.username =:us AND ors.createAt BETWEEN :from AND :to")
    public Page<Order> getByUsername(@Param("us")String user, @Param("from")Date from, @Param("to") Date to, Pageable pageable);
    @Query("SELECT ors FROM Order ors WHERE ors.account.username =:us AND ors.id =:id")
    public Optional<Order> getByIdAndUsername(@Param("us")String user, @Param("id") Integer id);
    @Query("SELECT ors FROM Order ors WHERE ors.paymentCode =:id")
    public Optional<Order> getByPaymentCode(@Param("id") String oid);
//    @Query("SELECT new com.application.dto.Sql.StatisticalDto(sum(od.orderDetails.quantity*od.originalPrice),sum(od.orderDetails.quantity*od.originalPrice)) FROM com.application.entity.Order od GROUP BY date(od.createAt)")
    @Query(value = "select sum(order_detail.quantity*order_detail.original_price) as 'expense',sum(order_detail.quantity*order_detail.sell_price) as 'profit',month(animeshop.order.create_at) as 'date'\n" +
            "\t from animeshop.order join order_detail on animeshop.order.id = order_detail.order_id" +
            " where animeshop.order.create_at between ? and ?" +
            "  group by month(animeshop.order.create_at) order by month(animeshop.order.create_at) asc ;",nativeQuery = true)
    public List<Map<String,Object>> statistical(Date from, Date to);
}
