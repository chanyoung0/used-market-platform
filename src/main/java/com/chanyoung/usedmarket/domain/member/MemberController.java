package com.chanyoung.usedmarket.domain.member;

import com.chanyoung.usedmarket.domain.member.dto.SignUpRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@RequestBody SignUpRequestDto requestDto) {
        Long memberId = memberService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);
    }
}
