package com.application.controller;

import com.application.dto.SupplierDto;
import com.application.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supplier")
@CrossOrigin("*")
public class RatioController {
    @Autowired
    SupplierService supplierService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCategory(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "5") Integer pageSize,
                                            @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                            @RequestParam(name = "status", required = false) Integer status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return new ResponseEntity<>(supplierService.getAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(supplierService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(supplierService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> insert(@RequestBody SupplierDto dto) {
        return new ResponseEntity<>(supplierService.insert(dto), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody SupplierDto dto) {
        return new ResponseEntity<>(supplierService.update(dto), HttpStatus.ACCEPTED);
    }

//    @PutMapping("/new")
//    public ResponseEntity<?> update(@RequestBody RatioDto dto) {
//        return new ResponseEntity<>(supplierService.update(dto), HttpStatus.ACCEPTED);
//    }
}
