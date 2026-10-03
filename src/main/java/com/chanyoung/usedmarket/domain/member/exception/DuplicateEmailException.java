package com.chanyoung.usedmarket.domain.member.exception;

import com.chanyoung.usedmarket.global.exception.BusinessException;
import com.chanyoung.usedmarket.global.exception.ErrorCode;

public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException() {
        super(ErrorCode.DUPLICATE_EMAIL);
    }
}
