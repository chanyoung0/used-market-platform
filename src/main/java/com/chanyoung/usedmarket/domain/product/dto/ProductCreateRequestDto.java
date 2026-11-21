package com.chanyoung.usedmarket.domain.product.dto;

import com.chanyoung.usedmarket.domain.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class ProductCreateRequestDto {

    @NotBlank
    @Size(max = 50)
    private String title;

    @NotBlank
    @Size(max = 5000)
    private String description;

    @NotNull
    private ProductCategory category;

    @NotNull
    @Positive
    private Integer price;

    @Size(max = 2048)
    private String imageUrl;
}
