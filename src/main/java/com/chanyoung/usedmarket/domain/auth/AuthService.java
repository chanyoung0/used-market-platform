package com.chanyoung.usedmarket.domain.auth;

import com.chanyoung.usedmarket.domain.auth.dto.LoginRequestDto;
import com.chanyoung.usedmarket.domain.auth.dto.LoginResponseDto;
import com.chanyoung.usedmarket.domain.auth.exception.InvalidCredentialsException;
import com.chanyoung.usedmarket.domain.member.Member;
import com.chanyoung.usedmarket.domain.member.MemberRepository;
import com.chanyoung.usedmarket.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        Member findMember = memberRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        boolean matches = passwordEncoder.matches(password, findMember.getPassword());
        if(!matches) {
            throw new InvalidCredentialsException();
        }
        String token = jwtTokenProvider.createToken(findMember.getId());
        return new LoginResponseDto(token);
    }

}
