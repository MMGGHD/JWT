package com.example.kakao.core.utils;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.kakao._core.utils.JwtTokenUtils;
import com.example.kakao.user.User;

public class JwtTokenUtilsTest {

    @Test
    public void jwt_created_test() {
        // 테스트 토큰 생성
        User user = User.builder().id(1).email("ssar@nate.com").build();
        String jwt = JwtTokenUtils.create(user);
        System.out.println(jwt);

        // 테스트 토큰 검증
        DecodedJWT decodedJWTOK = JwtTokenUtils.verify(jwt);
    }

    @Test
    public void jwt_verify_test() {

        // 테스트 토큰 생성
        User user = User.builder().id(1).email("ssar@nate.com").build();
        String jwt = JwtTokenUtils.create(user);

        // 테스트 토큰 변조
        // jwt = jwt.replaceAll("c", "k");

        // 테스트 토큰 검증
        DecodedJWT decodedJWTError = JwtTokenUtils.verify(jwt);

        // 검증하면 payload값을 찾아 출력해볼수 있다.
        int id = decodedJWTError.getClaim("id").asInt();
        String email = decodedJWTError.getClaim("email").asString();
        System.out.println("id : " + id);
        System.out.println("email : " + email);
    }
}
