package com.application.entity;

import com.application.dto.TypeProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "product_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeProduct extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Boolean status = true;

    public TypeProduct(TypeProductDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }
}
