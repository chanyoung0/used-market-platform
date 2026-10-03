package com.chanyoung.usedmarket.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {

    private final String accessToken;

    public LoginResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
