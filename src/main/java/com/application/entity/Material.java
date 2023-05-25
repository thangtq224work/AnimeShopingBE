package com.application.entity;

import com.application.dto.MaterialDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "material")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Material extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Boolean status = true;

    public Material(MaterialDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.name = dto.getName();
    }
}
