package com.application.dto;

import com.application.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private Integer id;
    private String name;
    private Boolean status;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.status = category.getStatus();
    }
}
