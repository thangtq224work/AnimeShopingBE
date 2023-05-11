package com.application.service.impl;

import com.application.common.PageData;
import com.application.dto.CategoryDto;
import com.application.dto.TypeProductDto;
import com.application.entity.Category;
import com.application.entity.TypeProduct;
import com.application.exception.NotFoundException;
import com.application.repository.TypeProductRepo;
import com.application.service.TypeProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeProductServiceImpl implements TypeProductService {
    private final TypeProductRepo typeProductRepo;

    @Override
    public PageData<TypeProductDto> getAll(Pageable pageable) {
        Page<TypeProduct> p = typeProductRepo.findAll(pageable);
        return PageData.of(p, p.toList().stream().map((i) -> new TypeProductDto(i)).collect(Collectors.toList()));
    }

    @Override
    public TypeProductDto getById(Integer id) {
        TypeProduct ratio = typeProductRepo.findById(id).orElseThrow(() -> new NotFoundException("TypeProduct not found"));
        return new TypeProductDto(ratio);
    }

    @Override
    public TypeProductDto insert(TypeProductDto dto) {
        dto.setId(null);
        TypeProduct ratio = typeProductRepo.save(new TypeProduct(dto));
        return new TypeProductDto(ratio);
    }

    @Override
    public TypeProductDto update(TypeProductDto dto) {
        this.getById(dto.getId());
        TypeProduct ratio = typeProductRepo.save(new TypeProduct(dto));
        return new TypeProductDto(ratio);
    }

    @Override
    public List<TypeProductDto> getAll() {
        List<TypeProduct> p = typeProductRepo.findAll();
        return p.stream().map((i) -> new TypeProductDto(i)).collect(Collectors.toList());
    }
}
