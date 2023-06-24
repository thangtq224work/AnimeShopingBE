package com.application.service;

import com.application.common.PageData;
import com.application.dto.request.OrderGhnReq;
import com.application.dto.request.OrderReq;
import com.application.dto.response.OrderResp;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface OrderService {
    int create(OrderReq orderReq);
    int confirm(Integer orderId);
    int delivery(OrderGhnReq orderId);
    int delivering(Integer orderId);
    int success(Integer orderId);
    int cancel(Integer orderId);
    PageData<OrderResp> getAll(Integer st, Integer p, Integer s);
}
