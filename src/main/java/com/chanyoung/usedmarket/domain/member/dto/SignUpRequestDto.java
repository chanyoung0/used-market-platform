package com.chanyoung.usedmarket.domain.member.dto;

import lombok.Getter;

@Getter
public class SignUpRequestDto {

    private String email;
    private String password;
    private String nickname;
}
