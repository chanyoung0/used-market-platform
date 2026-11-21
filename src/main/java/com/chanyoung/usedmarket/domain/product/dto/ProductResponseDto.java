package com.chanyoung.usedmarket.domain.product.dto;

import com.chanyoung.usedmarket.domain.member.Member;
import com.chanyoung.usedmarket.domain.product.Product;
import com.chanyoung.usedmarket.domain.product.ProductCategory;
import com.chanyoung.usedmarket.domain.product.ProductStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProductResponseDto {

    private Long id;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private SellerDto seller;
    private String title;
    private String description;
    private ProductCategory category;
    private Integer price;
    private String imageUrl;

    public static ProductResponseDto from(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.id = product.getId();
        dto.status = product.getStatus();
        dto.createdAt = product.getCreatedAt();
        dto.seller = SellerDto.from(product.getSeller());
        dto.title = product.getTitle();
        dto.description = product.getDescription();
        dto.category = product.getCategory();
        dto.price = product.getPrice();
        dto.imageUrl = product.getImageUrl();
        return dto;
    }

    @Getter
    public static class SellerDto{
        private Long id;
        private String nickname;

        static SellerDto from(Member member) {
            SellerDto dto = new SellerDto();
            dto.id = member.getId();
            dto.nickname = member.getNickname();
            return dto;
        }
    }
}

