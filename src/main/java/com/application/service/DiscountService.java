package com.application.service;

import com.application.common.PageData;
import com.application.dto.request.DiscountReq;
import org.springframework.data.domain.Pageable;

public interface DiscountService {
    DiscountReq save(DiscountReq voucherReq);
    DiscountReq update(DiscountReq voucherReq);
    void delete(Integer id);
    PageData<DiscountReq> findAll(Pageable pageable, String name, Boolean status, String from, String to);
    DiscountReq findById(Integer id);

}
