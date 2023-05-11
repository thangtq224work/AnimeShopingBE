package com.application.service;

import com.application.common.PageData;
import com.application.dto.CategoryDto;
import com.application.dto.TypeProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeProductService {
    PageData<TypeProductDto> getAll(Pageable pageable);

    List<TypeProductDto> getAll();

    TypeProductDto getById(Integer id);

    TypeProductDto insert(TypeProductDto dto);

    TypeProductDto update(TypeProductDto dto);


}
