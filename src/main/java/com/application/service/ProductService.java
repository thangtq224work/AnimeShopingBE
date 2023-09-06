package com.application.service;

import com.application.common.PageData;
import com.application.dto.ImageDto;
import com.application.dto.request.OrderReq;
import com.application.dto.request.ProductInCartReq;
import com.application.dto.request.ProductReq;
import com.application.dto.response.ProductResp;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    PageData<ProductResp> getAll(Pageable pageable,String search,Integer status);

    List<ProductResp> getAll();

    ProductResp getById(Integer id);

    PageData<ProductResp> getProduct(Integer page, Integer size, List<Integer> categories, List<Integer> materials, List<Integer> typeProduct, String sortBy, String direction);

    ProductResp insert(ProductReq dto);

    Map<String, Object> getFilter();

    ProductResp update(ProductReq dto) throws IOException;
    List<ProductResp> getProductInCart(List<ProductInCartReq> productInCartReqs);
    List<ProductResp> getProductInCartV2(List<OrderReq.Product> products);
    List<ImageDto> saveImages(MultipartFile[] files, Integer id) throws IOException;

    public Map<String, Object> show(String url) throws IOException;
}
