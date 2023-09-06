package com.application.service;

import com.application.common.PageData;
import com.application.dto.request.OrderGhnReq;
import com.application.dto.request.OrderReq;
import com.application.dto.response.OrderResp;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public interface OrderService {
    int create(OrderReq orderReq);
    int confirm(Integer orderId);
    int delivery(OrderGhnReq orderId);
    int delivering(Integer orderId);
    int success(Integer orderId);
    int cancel(Integer orderId);
    PageData<OrderResp> getOrder(String user, Date from, Date to, Pageable pageable);
    OrderResp getOrderById(Integer id);
    int refundPayment(Integer id, Authentication authentication, HttpServletRequest request) throws IOException;
    PageData<OrderResp> getAll(Integer st, Integer p, Integer s);
    public Object createPaymentOrder(Integer id, Authentication authentication, String locale, HttpServletRequest request) throws UnsupportedEncodingException;
    public int confirmPayment(HttpServletRequest ipnReq) throws UnsupportedEncodingException;
    String queryPayment(Integer oid,HttpServletRequest request) throws IOException;
}
