package com.application.service;

import com.application.common.PageData;
import com.application.dto.request.OrderReq;
import com.application.dto.response.OrderResp;

public interface OrderService {
    int create(OrderReq orderReq);
    PageData<OrderResp> getAll(Integer st, Integer p, Integer s);
}
