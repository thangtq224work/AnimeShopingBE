package com.application.dto;

import com.application.entity.Material;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDto {
    private Integer id;
    private String name;
    private Boolean status;

    public MaterialDto(Material material) {
        this.id = material.getId();
        this.name = material.getName();
        this.status = material.getStatus();
    }
}
