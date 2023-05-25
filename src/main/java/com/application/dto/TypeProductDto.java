package com.application.dto;

import com.application.entity.Category;
import com.application.entity.TypeProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeProductDto {
    private Integer id;
    private String name;
    private Boolean status;


    public TypeProductDto(TypeProduct typeProduct) {
        this.id = typeProduct.getId();
        this.name = typeProduct.getName();
        this.status = typeProduct.getStatus();
    }
}
