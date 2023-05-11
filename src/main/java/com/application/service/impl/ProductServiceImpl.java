package com.application.service.impl;

import com.application.common.PageData;
import com.application.dto.ProductDto;
import com.application.dto.ProductResp;
import com.application.entity.*;
import com.application.exception.NotFoundException;
import com.application.repository.*;
import com.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    CategoryRepo categoryRepo;
    @Autowired
    TypeProductRepo typeProductRepo;
    @Autowired
    MaterialRepo materialRepo;
    @Autowired
    SupplierRepo supplierRepo;
    @Autowired
    ProductRepo productRepo;

    @Override
    public PageData<ProductResp> getAll(Pageable pageable) {
        Page<Product> page = productRepo.findAll(pageable);
        return PageData.of(page,page.toList().stream().map(i->new ProductResp(i)).collect(Collectors.toList()));
    }

    @Override
    public List<ProductResp> getAll() {
        return null;
    }

    @Override
    public ProductResp getById(Integer id) {
        Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
        return new ProductResp(product);
    }

    @Override
    public ProductResp insert(ProductDto dto) {
        Category category = categoryRepo.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Category not found"));
        Material material = materialRepo.findById(dto.getMaterial()).orElseThrow(() -> new NotFoundException("Material not found"));
        Supplier supplier = supplierRepo.findById(dto.getSupplier()).orElseThrow(() -> new NotFoundException("Supplier not found"));
        TypeProduct typeProduct = typeProductRepo.findById(dto.getTypeProduct()).orElseThrow(() -> new NotFoundException("TypeProduct not found"));
        Product product = new Product(dto);
        product.setCategory(category);
        product.setMaterial(material);
        product.setSupplier(supplier);
        product.setTypeProduct(typeProduct);
        product = productRepo.save(product);
        return new ProductResp(product);
    }

    @Override
    public ProductResp update(ProductDto dto) {
        Product product = productRepo.findById(dto.getId()).orElseThrow(() -> new NotFoundException("Product not found"));
        Category category = categoryRepo.findById(dto.getCategory()).orElseThrow(() -> new NotFoundException("Category not found"));
        Material material = materialRepo.findById(dto.getMaterial()).orElseThrow(() -> new NotFoundException("Material not found"));
        Supplier supplier = supplierRepo.findById(dto.getSupplier()).orElseThrow(() -> new NotFoundException("Supplier not found"));
        TypeProduct typeProduct = typeProductRepo.findById(dto.getTypeProduct()).orElseThrow(() -> new NotFoundException("TypeProduct not found"));
        product.setCategory(category);
        product.setMaterial(material);
        product.setSupplier(supplier);
        product.setTypeProduct(typeProduct);
        product.convert(dto);
        product = productRepo.save(product);
        return new ProductResp(product);
    }
}
