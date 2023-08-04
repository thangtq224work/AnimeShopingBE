package com.application.controller;

import com.application.common.ResponseDataTemplate;
import com.application.dto.request.OrderReq;
import com.application.dto.request.ProductInCartReq;
import com.application.service.OrderService;
import com.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/get-filter")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(productService.getFilter(), HttpStatus.OK);
    }

    @GetMapping("/get-product")
    public ResponseEntity<?> getProduct(@RequestParam(name = "sortBy", required = false) String sortBy,
                                        @RequestParam(name = "dir", required = false, defaultValue = "asc") String direction,
                                        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                        @RequestParam(name = "size", required = false, defaultValue = "12") Integer pageSize,
                                        @RequestParam(name = "categories", required = false) List<Integer> categories,
                                        @RequestParam(name = "materials", required = false) List<Integer> materials,
                                        @RequestParam(name = "typeProducts", required = false) List<Integer> typeProducts) {
        return new ResponseEntity<>(productService.getProduct(page, pageSize, categories, materials, typeProducts, sortBy, direction), HttpStatus.OK);
    }
    @PostMapping("/get-cart")
    public ResponseEntity<?> getCart(@RequestBody() List<ProductInCartReq> productInCartReqs) {
        return new ResponseEntity<>(productService.getProductInCart(productInCartReqs), HttpStatus.OK);
    }
    @GetMapping("/show")
    @Cacheable(key = "#url", cacheManager = "imageCache", value = "images")
    public ResponseEntity<byte[]> show(@RequestParam("url") String url) throws IOException {
        Map<String, Object> map = productService.show(url);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType((String) map.get("contentType")))
                .body((byte[]) map.get("image"));
    }
    @PostMapping("/order/create")
    public ResponseEntity<?> order(@RequestBody OrderReq orderReq){
        return new ResponseEntity<>(ResponseDataTemplate.OK.data(orderService.create(orderReq)).build(), HttpStatus.OK);
    }
}
