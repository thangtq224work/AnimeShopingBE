package com.application.service.impl;

import com.application.common.PageData;
import com.application.dto.CategoryDto;
import com.application.dto.MaterialDto;
import com.application.entity.Category;
import com.application.entity.Material;
import com.application.exception.NotFoundException;
import com.application.repository.MaterialRepo;
import com.application.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {
    private final MaterialRepo materialRepo;

    @Override
    public PageData<MaterialDto> getAll(Pageable pageable) {
        Page<Material> p = materialRepo.findAll(pageable);
        return PageData.of(p, p.toList().stream().map((i) -> new MaterialDto(i)).collect(Collectors.toList()));
    }

    @Override
    public MaterialDto getById(Integer id) {
        Material material = materialRepo.findById(id).orElseThrow(() -> new NotFoundException("Material not found"));
        return new MaterialDto(material);
    }

    @Override
    public MaterialDto insert(MaterialDto dto) {
        dto.setId(null);
        Material material = materialRepo.save(new Material(dto));
        return new MaterialDto(material);
    }

    @Override
    public MaterialDto update(MaterialDto dto) {
        this.getById(dto.getId());
        Material material = materialRepo.save(new Material(dto));
        return new MaterialDto(material);
    }

    @Override
    public List<MaterialDto> getAll() {
        List<Material> p = materialRepo.findAll();
        return p.stream().map((i) -> new MaterialDto(i)).collect(Collectors.toList());
    }
}
