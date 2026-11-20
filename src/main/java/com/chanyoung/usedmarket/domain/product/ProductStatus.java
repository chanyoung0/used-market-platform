package com.chanyoung.usedmarket.domain.product;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ON_SALE("판매중"),
    RESERVED("예약중"),
    SOLD("판매완료");

    private final String displayName;


    ProductStatus(String displayName) {
        this.displayName = displayName;
    }
}
