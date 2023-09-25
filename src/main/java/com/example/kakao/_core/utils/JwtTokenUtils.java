package com.example.kakao._core.utils;

import java.time.Instant;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.kakao.user.User;

public class JwtTokenUtils {

    public static String create(User user) {
        String jwt = JWT.create()
                // withSubject << 토큰의 이름(아무거나 상관X), 반드시 있어야함(프로토콜)
                .withSubject("metacoding-key")
                // withClaim << 전자서명할 대상 (key,value)로 구성, 여러개 입력가능
                .withClaim("id", user.getId())
                .withClaim("email", user.getEmail())
                // withExpiresAt << 만료되는 시간(중요)
                // Instant.now() << 현재시간, plusMillis << 지속시간
                .withExpiresAt(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7L))
                // sign << 인증 방식, HMAC512를 사용. 매개변수로는 Secret-Key
                // Secret-Key << 보안 가장 중요, 원래는 OS에 저장, 접근 막고 환경변수로 씀
                .sign(Algorithm.HMAC512("meta"));

        return "Bearer " + jwt;
    }

    // jwt인증방식을 Bearer 인증방식이라고 한다. << 통신을 할때 앞쪽에 "Bearer "를 붙여준다.
    // 검증할때는 상관없다. (그래서 replace를 씀)
    // 검증 실패 시나리오
    // 1. 토큰 시간 만료
    // 2. 토큰이 Key값과 대응되지 않음
    public static DecodedJWT verify(String jwt)
            throws SignatureVerificationException, TokenExpiredException {
        jwt = jwt.replace("Bearer ", "");

        // DecodedJWT 객체가 생성되면 검증이 올바르게 되었다.
        // DecodedJWT는 Header와 Payload를 검증하고 base64로 디코딩한 값을 담는다.
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("meta"))
                .build().verify(jwt);
        return decodedJWT;
    }

}
