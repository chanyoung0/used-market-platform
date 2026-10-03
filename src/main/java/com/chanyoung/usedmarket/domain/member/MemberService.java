package com.chanyoung.usedmarket.domain.member;

import com.chanyoung.usedmarket.domain.member.dto.MemberResponseDto;
import com.chanyoung.usedmarket.domain.member.dto.MemberUpdateRequestDto;
import com.chanyoung.usedmarket.domain.member.dto.SignUpRequestDto;
import com.chanyoung.usedmarket.domain.member.exception.DuplicateEmailException;
import com.chanyoung.usedmarket.domain.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(SignUpRequestDto request) {
        if(memberRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member member = Member.createUser(request.getEmail(), encodedPassword, request.getNickname());
        Member savedMember = memberRepository.save(member);

        return savedMember.getId();

    }


    @Transactional(readOnly = true)
    public MemberResponseDto getMyInfo(Long memberId){
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return MemberResponseDto.from(findMember);

    }

    @Transactional
    public void updateMyInfo(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        findMember.changeNickname(memberUpdateRequestDto.getNickname());

    }


}
