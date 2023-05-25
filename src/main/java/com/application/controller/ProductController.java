package com.application.controller;

import com.application.dto.ProductDto;
import com.application.dto.ProductResp;
import com.application.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllProperty(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "5") Integer pageSize,
                                            @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                            @RequestParam(name = "status", required = false) Integer status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return new ResponseEntity<>(productService.getAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/get-all-")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(productService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(productService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> insert(@RequestBody @Valid ProductDto dto) {
        return new ResponseEntity<>(productService.insert(dto), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody @Valid ProductDto dto) throws IOException {
        return new ResponseEntity<>(productService.update(dto), HttpStatus.ACCEPTED);
    }
    @PostMapping("/image-upload/{id}")
    public ResponseEntity<?> uploadImages(@RequestParam("images") MultipartFile[] files,@PathVariable("id") Integer id) throws IOException {
        return ResponseEntity.ok(productService.saveImages(files,id));
    }
    @GetMapping("/show")
    public ResponseEntity<byte[]> show(@RequestParam("url") String url) throws IOException {
        Map<String, Object> map = productService.show(url);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType((String) map.get("contentType")))
                .body((byte[]) map.get("image"));
    }
//    @PutMapping("/new")
//    public ResponseEntity<?> update(@RequestBody ProductDto dto) {
//        return new ResponseEntity<>(productService.update(dto), HttpStatus.ACCEPTED);
//    }
//    public
}
