package com.application.service.impl;

import com.application.common.PageData;
import com.application.dto.CategoryDto;
import com.application.entity.Category;
import com.application.exception.NotFoundException;
import com.application.repository.CategoryRepo;
import com.application.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    @Override
    public PageData<CategoryDto> getAll(Pageable pageable) {
        Page<Category> p = categoryRepo.findAll(pageable);
        return PageData.of(p, p.toList().stream().map((i) -> new CategoryDto(i)).collect(Collectors.toList()));
    }

    @Override
    public CategoryDto getById(Integer id) {
        Category category = categoryRepo.findById(id).orElseThrow(() -> new NotFoundException("Category not found"));
        return new CategoryDto(category);
    }

    @Override
    public CategoryDto insert(CategoryDto dto) {
        dto.setId(null);
        Category category = categoryRepo.save(new Category(dto));
        return new CategoryDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        this.getById(dto.getId());
        Category category = categoryRepo.save(new Category(dto));
        return new CategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAll() {
        List<Category> p = categoryRepo.findAll();
        return p.stream().map((i) -> new CategoryDto(i)).collect(Collectors.toList());
    }
}
