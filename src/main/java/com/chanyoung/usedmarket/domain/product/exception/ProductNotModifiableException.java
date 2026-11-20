package com.chanyoung.usedmarket.domain.product.exception;

import com.chanyoung.usedmarket.global.exception.BusinessException;
import com.chanyoung.usedmarket.global.exception.ErrorCode;

public class ProductNotModifiableException extends BusinessException {
    public ProductNotModifiableException() {
        super(ErrorCode.PRODUCT_NOT_MODIFIABLE);
    }
}
