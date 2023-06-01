package com.application.entity;

import com.application.dto.ProductDto;
import com.application.dto.ProductResp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Float weight;
    private Float width;
    private Float height;
    private Float length;
    private BigDecimal price;
    private BigDecimal priceSell;
    private Boolean status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_product_id")
    private TypeProduct typeProduct;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    public Product(Integer id) {
        this.id = id;
    }

    public Product(ProductDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.weight = dto.getWeight();
        this.height = dto.getHeight();
        this.length = dto.getLength();
        this.width = dto.getWidth();
        this.price = dto.getPrice();
        this.priceSell = dto.getPriceSell();
    }

    public void convert(ProductDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.weight = dto.getWeight();
        this.height = dto.getHeight();
        this.length = dto.getLength();
        this.width = dto.getWidth();
        this.price = dto.getPrice();
        this.priceSell = dto.getPriceSell();
    }

}
