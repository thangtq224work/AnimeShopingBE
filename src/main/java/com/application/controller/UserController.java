package com.application.controller;

import com.application.dto.request.ProductInCartReq;
import com.application.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class UserController {
    @Autowired
    private ProductService productService;

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
}
