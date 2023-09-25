package com.example.kakao.user;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.kakao._core.errors.exception.Exception400;
import com.example.kakao._core.errors.exception.Exception500;
import com.example.kakao._core.utils.JwtTokenUtils;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserJPARepository userJPARepository;

    @Transactional
    public void join(UserRequest.JoinDTO requestDTO) {
        try {
            userJPARepository.save(requestDTO.toEntity());
        } catch (Exception e) {
            throw new Exception500("unknown server error");
        }
    }

    public String login(UserRequest.LoginDTO requestDTO) {
        User userPS = userJPARepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new Exception400("email을 찾을 수 없습니다 : " + requestDTO.getEmail()));

        return JwtTokenUtils.create(userPS);
    }

    // 토큰 생성 연습
    public String loginPractice(UserRequest.LoginDTO requestDTO) {
        User userPS = userJPARepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new Exception400("email을 찾을 수 없습니다 : " + requestDTO.getEmail()));

        // JWT << 내부가 Builder 패턴으로 만들어져있다.
        // 검증은 Filter에 만들어서 한다. (그렇지 않을경우 컨트롤러마다 검증해야함)
        // 토큰을 받으면 Header의 Authorizion의 값으로 확인할수 있다.
        String jwt = JWT.create()
                // withSubject << 토큰의 이름(아무거나 상관X), 반드시 있어야함(프로토콜)
                .withSubject("metacoding-key")
                // withClaim << 전자서명할 대상 (key,value)로 구성, 여러개 입력가능
                .withClaim("id", userPS.getId())
                .withClaim("email", userPS.getEmail())
                // withExpiresAt << 만료되는 시간(중요)
                // Instant.now() << 현재시간, plusMillis << 지속시간
                .withExpiresAt(Instant.now().plusMillis(1000 * 60 * 60 * 24 * 7L))
                // sign << 인증 방식, HMAC512를 사용. 매개변수로는 Secret-Key
                // Secret-Key << 보안 가장 중요, 원래는 OS에 저장, 접근 막고 환경변수로 씀
                .sign(Algorithm.HMAC512("meta"));

        return jwt;
    }
}
