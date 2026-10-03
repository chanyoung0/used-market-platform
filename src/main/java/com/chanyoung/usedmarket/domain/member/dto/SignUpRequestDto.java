package com.chanyoung.usedmarket.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 7, max = 18)
    private String password;

    @NotBlank
    @Size(min = 2, max = 8)
    private String nickname;
}
