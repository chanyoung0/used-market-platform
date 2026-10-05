package com.chanyoung.usedmarket.global.security;

import com.chanyoung.usedmarket.global.exception.InvalidTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);
            try {
                Long memberId = jwtTokenProvider.getMemberId(token);
                // TODO: 현재 role이 USER 하나뿐이라 권한 목록을 비워둠. 역할 구분이 필요해지면 토큰에 role을 담아 채울 것
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(memberId, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (InvalidTokenException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("errorCode", e.getErrorCode());
            }

        }

        filterChain.doFilter(request, response);
    }
}
