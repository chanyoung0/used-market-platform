package com.chanyoung.usedmarket.domain.member.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateRequestDto {

    @NotBlank
    @Size(min = 2, max = 8)
    private String nickname;

}
