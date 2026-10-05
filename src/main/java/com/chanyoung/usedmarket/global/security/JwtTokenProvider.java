package com.chanyoung.usedmarket.global.security;

import com.chanyoung.usedmarket.global.exception.InvalidTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final byte[] secretKey;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public String createToken(Long memberId) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expirationMs);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(String.valueOf(memberId))
                .issueTime(issuedAt)
                .expirationTime(expiresAt)
                .build();

        SignedJWT token = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);

        try {
            token.sign(new MACSigner(secretKey));
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT 서명에 실패했습니다.", e);
        }

        return token.serialize();
    }

    //verify 검증, 토큰 만료 시간 검증
    public Long getMemberId(String token) {
        try {
            SignedJWT parsed = SignedJWT.parse(token);
            if(!parsed.verify(new MACVerifier(secretKey))){
                throw new InvalidTokenException();
            }
            JWTClaimsSet claims = parsed.getJWTClaimsSet();
            Date expiresAt = claims.getExpirationTime();

            if(expiresAt == null || expiresAt.before(new Date())){
                throw new InvalidTokenException();
            }
            return Long.valueOf(claims.getSubject());
        } catch (ParseException | JOSEException e) {
            throw new InvalidTokenException();
        }

    }
}