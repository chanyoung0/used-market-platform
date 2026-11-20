package com.chanyoung.usedmarket.domain.product;

import lombok.Getter;

@Getter
public enum ProductCategory {
    DIGITAL("디지털/가전"),
    FURNITURE("가구/인테리어"),
    CLOTHING("의류/패션잡화"),
    BOOK("도서"),
    LIVING("생활/주방"),
    SPORTS("스포츠/레저"),
    ETC("기타");

    private final String displayName;


    ProductCategory(String displayName) {
        this.displayName = displayName;
    }
}
