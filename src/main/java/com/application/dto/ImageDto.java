package com.application.dto;

import com.application.entity.ProductImage;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ImageDto {
    private Integer id;
    private String url;

    public ImageDto(ProductImage image) {
        this.id = image.getId();
        this.url = image.getUrl();
    }
}
