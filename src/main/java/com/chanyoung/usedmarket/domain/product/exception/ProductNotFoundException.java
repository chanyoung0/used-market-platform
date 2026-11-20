package com.chanyoung.usedmarket.domain.product.exception;

import com.chanyoung.usedmarket.global.exception.BusinessException;
import com.chanyoung.usedmarket.global.exception.ErrorCode;

public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException() {
        super(ErrorCode.PRODUCT_NOT_FOUND);
    }
}
