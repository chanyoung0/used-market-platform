package com.chanyoung.usedmarket.global.security;

import com.chanyoung.usedmarket.global.exception.ErrorCode;
import com.chanyoung.usedmarket.global.exception.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("errorCode");
        if (errorCode == null) {
            errorCode = ErrorCode.AUTHENTICATION_REQUIRED;
        }
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponseDto dto = new ErrorResponseDto(errorCode);

        String json = objectMapper.writeValueAsString(dto);
        response.getWriter().write(json);
    }
}
