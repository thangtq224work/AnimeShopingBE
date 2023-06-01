package com.application.service.impl;

import com.application.common.PageData;
import com.application.constant.Constant;
import com.application.dto.SupplierDto;
import com.application.entity.Supplier;
import com.application.exception.NotFoundException;
import com.application.repository.SupplierRepo;
import com.application.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepo supplierRepo;

    @Override
    public PageData<SupplierDto> getAll(Pageable pageable) {
        Page<Supplier> p = supplierRepo.findAll(pageable);
        return PageData.of(p, p.toList().stream().map((i) -> new SupplierDto(i)).collect(Collectors.toList()));
    }

    @Override
    public SupplierDto getById(Integer id) {
        Supplier ratio = supplierRepo.findById(id).orElseThrow(() -> new NotFoundException("Supplier not found"));
        return new SupplierDto(ratio);
    }

    @Override
    public SupplierDto insert(SupplierDto dto) {
        dto.setId(null);
        Supplier ratio = supplierRepo.save(new Supplier(dto));
        return new SupplierDto(ratio);
    }

    @Override
    public SupplierDto update(SupplierDto dto) {
        this.getById(dto.getId());
        Supplier ratio = supplierRepo.save(new Supplier(dto));
        return new SupplierDto(ratio);
    }

    @Override
    public List<SupplierDto> getAll() {
        List<Supplier> p = supplierRepo.findAll();
        return p.stream().map((i) -> new SupplierDto(i)).collect(Collectors.toList());
    }
}
