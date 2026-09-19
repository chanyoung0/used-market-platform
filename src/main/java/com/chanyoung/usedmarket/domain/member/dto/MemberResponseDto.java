package com.chanyoung.usedmarket.domain.member.dto;

import com.chanyoung.usedmarket.domain.member.Member;
import com.chanyoung.usedmarket.domain.member.Role;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private Role role;
    private LocalDateTime createdAt;

    public static MemberResponseDto from(Member member) {
        MemberResponseDto dto = new MemberResponseDto();
        dto.id = member.getId();
        dto.email = member.getEmail();
        dto.nickname = member.getNickname();
        dto.role = member.getRole();
        dto.createdAt = member.getCreatedAt();

        return dto;

    }
}
