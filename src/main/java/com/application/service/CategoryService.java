package com.application.service;

import com.application.common.PageData;
import com.application.dto.CategoryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    PageData<CategoryDto> getAll(Pageable pageable);

    List<CategoryDto> getAll();

    CategoryDto getById(Integer id);

    CategoryDto insert(CategoryDto dto);

    CategoryDto update(CategoryDto dto);


}
