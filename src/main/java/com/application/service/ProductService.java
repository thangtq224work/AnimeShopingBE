package com.application.service;

import com.application.common.PageData;
import com.application.dto.ProductDto;
import com.application.dto.ProductResp;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    PageData<ProductResp> getAll(Pageable pageable);

    List<ProductResp> getAll();

    ProductResp getById(Integer id);

    ProductResp insert(ProductDto dto);

    ProductResp update(ProductDto dto);
}
