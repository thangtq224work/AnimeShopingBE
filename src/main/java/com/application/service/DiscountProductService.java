package com.application.service;

import com.application.common.PageData;
import com.application.dto.request.ProductDiscountReq;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscountProductService {
        public PageData<ProductDiscountReq> getById(Integer id, Pageable pageable, Integer[] category, String name);
        ProductDiscountReq remove(ProductDiscountReq ProductDiscountReq);
        ProductDiscountReq save(ProductDiscountReq ProductDiscountReq);
        List<ProductDiscountReq> removeAll(List<ProductDiscountReq> ProductDiscountReq);
        List<ProductDiscountReq> saveAll(List<ProductDiscountReq> ProductDiscountReq);
}
