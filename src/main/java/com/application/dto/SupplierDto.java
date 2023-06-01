package com.application.dto;

import com.application.entity.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDto {
    private Integer id;
    private String name;
    private Boolean status;

    public SupplierDto(Supplier supplier) {
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.status = supplier.getStatus();
    }
}
