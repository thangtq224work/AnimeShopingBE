package com.application.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "product_image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductImage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String url;
    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;
//    @ManyToOne
    @Override
    public String toString(){
        return id+" : " +url;
    }
}
