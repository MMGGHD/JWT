package com.example.kakao._core.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.kakao._core.errors.exception.Exception401;
import com.example.kakao._core.utils.JwtTokenUtils;
import com.example.kakao.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

// Authorization << 인가(토큰유무에 따라 허용할지 안할지) != 인증(신뢰해서 토큰을 만들어줌)
// products, carts, orders로 시작하는 요청 주소는 인증검사가 필요함
public class JwtAuthorizationFilter implements Filter {

    // doFilter를 오버라이드 해야함
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        // ServletRequest는 최상위 부모클래스 << req를 다운캐스팅하여 더 많은 메서드 사용가능
        HttpServletRequest request = (HttpServletRequest) req;

        // ServletResponse 최상위 부모클래스 << resp를 다운캐스팅하여 더 많은 메서드 사용가능
        HttpServletResponse response = (HttpServletResponse) resp;

        // 쿠키는 Http프로토콜이기 때문에 브라우저가 알아서 쿠키를 저장하고 요청시 가지고감
        // 반면에 토큰은 그런과정이 없기 때문에 저장, 인가를 개발자가 직접 처리해줘야 한다.

        // products, carts, orders로 시작하는 요청 주소는 인증검사가 필요함
        // 나머지 요청주소들은 필터를 거치지 않고 통과해야한다.

        // header에서 "Authorization(토큰)"이있는지 확인. 있으면 검증하러ㄱ, 없으면 더 보내지않고 예외
        // JWT관련 Exception은 크게 3가지가 있다.
        String jwt = request.getHeader("Authorization");
        if (jwt == null || jwt.isEmpty()) {
            onError(response, "토큰이 없습니다.");

            // 여기서 코드 종료(return)
            return;
        }

        try {

            DecodedJWT decodedJWT = JwtTokenUtils.verify(jwt);
            int userId = decodedJWT.getClaim("id").asInt();
            String email = decodedJWT.getClaim("email").asString();

            // 컨트롤러에서 꺼내쓰기 쉽게 하려고 User객체로 Build하여 Session에 보관한다.
            User sessionUser = User.builder().id(userId).email(email).build();

            HttpSession session = request.getSession();

            session.setAttribute("sessionUser", sessionUser);

            // doFilter << 통과, 다음체인을 타라
            chain.doFilter(request, response);

            // JWT관련 Exception이 크게 3가지가 있다.
            // SignatureVerificationException << 검증실패
        } catch (SignatureVerificationException | JWTDecodeException e1) {
            onError(response, "토큰 검증 실패");
        }

        // TokenExpiredException << 토큰이 만료됨
        catch (TokenExpiredException e2) {
            onError(response, "토큰 시간 만료");
        }

    }

    // Filter는 DS보다 앞단에 위치, ExceptionHandler를 호출할수없다. << 손코딩 해야함
    private void onError(HttpServletResponse response, String msg) {

        Exception401 e401 = new Exception401("인증되지 않았습니다");

        try {
            // MessageConverter의 도움을 받지 못하기 때문에 ObjectMapper를 써서 직접파싱
            // writeValueAsString(데이터) << 데이터의 값을 Json문자열로 변환하는 메서드
            String body = new ObjectMapper().writeValueAsString(e401.body());

            // response 상태코드 작성
            response.setStatus(e401.status().value());

            // 위에서 Json문자열로 변환했으므로 헤더에 데이터타입을 명시해줘야 함
            // response.setContentType(MediaType.APPLICATION_JSON_VALUE); 아래코드와 같다.
            response.setHeader("Content-Type", "application/json; charset=utf-8");

            // 응답(BufferedWriter)버퍼에 연결 (PrintWriter << 데이터를 3바이트씩 집어넣어준다.)
            PrintWriter out = response.getWriter();

            // JSON데이터(body)를 버퍼에 작성한다.
            out.println(body);
        } catch (Exception e) {
            System.out.println("파싱 에러가 날 수 없음");
        }
    }
}
