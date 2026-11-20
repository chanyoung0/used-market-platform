package com.chanyoung.usedmarket.domain.product;

import com.chanyoung.usedmarket.domain.member.Member;
import com.chanyoung.usedmarket.domain.product.exception.ProductNotModifiableException;
import com.chanyoung.usedmarket.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;




@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private Member seller;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private ProductCategory category;

    @Column(nullable = false)
    private int price;

    @Column(length = 2048)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Builder
    private Product(Member seller, String title, String description, ProductCategory category, int price, String imageUrl) {
        this.seller = seller;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.status = ProductStatus.ON_SALE;
    }


    public static Product createProduct(Member seller, String title, String description, ProductCategory category, int price, String imageUrl) {
        imageUrl = normalizeImageUrl(imageUrl);

        return Product.builder()
                .seller(seller)
                .title(title)
                .description(description)
                .category(category)
                .price(price)
                .imageUrl(imageUrl)
                .build();
    }

    public void updateProduct(String title, String description, ProductCategory category, int price, String imageUrl){
        validateOnSale();

        imageUrl = normalizeImageUrl(imageUrl);

        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public boolean isSeller(Long memberId) {
        return this.seller.getId().equals(memberId);
    }

    //서비스에서 쓸 메서드
    public void validateDeletable(){
        // TODO: Transaction 단계에서 구매 요청이 있는 상품은 소프트 삭제하도록 분기 추가 (ADR 0005)
        validateOnSale();
    }

    private void validateOnSale(){
        if (status != ProductStatus.ON_SALE){
            throw new ProductNotModifiableException();
        }
    }

    private static String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }
        return imageUrl;
    }

}