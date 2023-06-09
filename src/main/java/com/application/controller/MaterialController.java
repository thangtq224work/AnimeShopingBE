package com.application.controller;

import com.application.dto.MaterialDto;
import com.application.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/material")
@CrossOrigin("*")
public class MaterialController {
    @Autowired
    MaterialService materialService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllProperty(@RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                            @RequestParam(name = "size", required = false, defaultValue = "5") Integer pageSize,
                                            @RequestParam(name = "search", required = false, defaultValue = "") String search,
                                            @RequestParam(name = "status", required = false) Integer status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        return new ResponseEntity<>(materialService.getAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(materialService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(materialService.getById(id), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<?> insert(@RequestBody MaterialDto dto) {
        return new ResponseEntity<>(materialService.insert(dto), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody MaterialDto dto) {
        return new ResponseEntity<>(materialService.update(dto), HttpStatus.ACCEPTED);
    }

//    @PutMapping("/new")
//    public ResponseEntity<?> update(@RequestBody MaterialDto dto) {
//        return new ResponseEntity<>(materialService.update(dto), HttpStatus.ACCEPTED);
//    }
}
