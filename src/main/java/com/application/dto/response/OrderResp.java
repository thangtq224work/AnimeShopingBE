package com.application.dto.response;

import com.application.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResp {
    private Integer id;
    private String clientName;
    private String address;
    private String phone;
    private BigDecimal shippingFee;
    private BigDecimal totalPrice;
    private String description;
    private String addressCode;
    private Integer status;
    private String userId;
    List<OrderResp.OrderDetail> orderDetails;
    public OrderResp(Order order){
        this.id= order.getId();;
        this.address = order.getAddress();
        this.phone = order.getPhone();
        this.description = order.getDescription();
        this.addressCode = order.getAddressCode();
        this.shippingFee = order.getShippingFee();
        this.totalPrice = order.getTotalPrice();
        this.clientName = order.getClientName();
        this.status = order.getStatus();
        this.userId = order.getAccount().getUsername();
        this.orderDetails = order.getOrderDetails().stream().map(i->new OrderResp.OrderDetail(i)).collect(Collectors.toList());
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderDetail{
        private Long id;
        private Integer quantity;
        private BigDecimal originalPrice;
        private BigDecimal sellPrice;
        private Float weight;
        private Float width;
        private Float height;
        private Float length;
        public OrderDetail(com.application.entity.OrderDetail orderDetail){
            this.id = orderDetail.getId();
            this.quantity = orderDetail.getQuantity();
            this.originalPrice = orderDetail.getOriginalPrice();
            this.sellPrice = orderDetail.getSellPrice();
            this.weight = orderDetail.getProduct().getWeight();
            this.width = orderDetail.getProduct().getWidth();
            this.height = orderDetail.getProduct().getHeight();
            this.length = orderDetail.getProduct().getLength();
        }
    }
}
