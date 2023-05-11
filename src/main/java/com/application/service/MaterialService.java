package com.application.service;

import com.application.common.PageData;
import com.application.dto.CategoryDto;
import com.application.dto.MaterialDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MaterialService {
    PageData<MaterialDto> getAll(Pageable pageable);

    List<MaterialDto> getAll();

    MaterialDto getById(Integer id);

    MaterialDto insert(MaterialDto dto);

    MaterialDto update(MaterialDto dto);


}
