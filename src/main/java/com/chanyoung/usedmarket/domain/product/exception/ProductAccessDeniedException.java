package com.chanyoung.usedmarket.domain.product.exception;

import com.chanyoung.usedmarket.global.exception.BusinessException;
import com.chanyoung.usedmarket.global.exception.ErrorCode;

public class ProductAccessDeniedException extends BusinessException {
    public ProductAccessDeniedException() {
        super(ErrorCode.PRODUCT_ACCESS_DENIED);
    }
}
