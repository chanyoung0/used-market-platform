package com.chanyoung.usedmarket.domain.member.exception;

import com.chanyoung.usedmarket.global.exception.BusinessException;
import com.chanyoung.usedmarket.global.exception.ErrorCode;

public class MemberNotFoundException extends BusinessException {

    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
