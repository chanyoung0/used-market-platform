package com.chanyoung.usedmarket.domain.member;

import com.chanyoung.usedmarket.domain.member.dto.MemberResponseDto;
import com.chanyoung.usedmarket.domain.member.dto.MemberUpdateRequestDto;
import com.chanyoung.usedmarket.domain.member.dto.SignUpRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signUp(@Valid @RequestBody SignUpRequestDto requestDto) {
        Long memberId = memberService.signUp(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> getMyInfo(@AuthenticationPrincipal Long memberId){
        return ResponseEntity.ok(memberService.getMyInfo(memberId));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyInfo(@AuthenticationPrincipal Long memberId, @Valid @RequestBody MemberUpdateRequestDto updateRequestDto){
        memberService.updateMyInfo(memberId ,updateRequestDto);
        return ResponseEntity.noContent().build();
    }
}
