package com.application.service;

import com.application.common.PageData;
import com.application.dto.SupplierDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {
    PageData<SupplierDto> getAll(Pageable pageable);

    List<SupplierDto> getAll();

    SupplierDto getById(Integer id);

    SupplierDto insert(SupplierDto dto);

    SupplierDto update(SupplierDto dto);


}
