package com.application.service;

import com.application.common.PageData;
import com.application.dto.ImageDto;
import com.application.dto.ProductDto;
import com.application.dto.ProductResp;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    PageData<ProductResp> getAll(Pageable pageable);

    List<ProductResp> getAll();

    ProductResp getById(Integer id);

    ProductResp insert(ProductDto dto);

    ProductResp update(ProductDto dto) throws IOException;
    List<ImageDto> saveImages(MultipartFile[] files, Integer id) throws IOException;
    public Map<String,Object> show(String url) throws IOException;
}
