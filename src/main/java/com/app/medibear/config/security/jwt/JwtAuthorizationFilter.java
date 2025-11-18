package com.app.medibear.config.security.jwt;

import com.app.medibear.config.security.auth.PrincipalDetails;
import com.app.medibear.config.security.oauth2.JwtProperties;
import com.app.medibear.entity.Member;
import com.app.medibear.repository.MemberRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


//시큐리티가 filter를 가지고 있는데  그 필터중에서 BasicAuthenticationFilter라는 것이 있다.
//권한이나 인증이 필요한 특정한 주소를 요청했을 떄 위의 필터를 무조건 콜하게 되어있다.
//만약에 권한이 인증이 필요한 주소가 아니라면 이 필터를 거치지 않는다.

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;
    private final JwtProperties jwtProperties;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtProperties jwtProperties) {

        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인가 시작!!!");

        String jwtHeader = request.getHeader("Authorization");
        System.out.println("jwtHeader = " + jwtHeader);

        String uri = request.getRequestURI();

        // refresh API는 인증 필터 실행 X
        if (uri.equals("/api/auth/refresh")) {
            chain.doFilter(request, response);
            return;
        }

        // Authorization 헤더 없음 → 다음 필터로 넘김
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtHeader.replace("Bearer ", "");
        String memberId = null;

        try {
            memberId = JWT.require(Algorithm.HMAC512(jwtProperties.getSecret()))
                .build()
                .verify(jwtToken)
                .getClaim("memberId").asString();

        } catch (TokenExpiredException e) {
            // 🔥 AccessToken 만료 → React가 refresh 시도하도록 code 포함해 반환
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            Map<String, String> result = new HashMap<>();
            result.put("code", "EXPIRED_ACCESS_TOKEN");
            result.put("message", "AccessToken has expired.");

            response.getWriter().write(new ObjectMapper().writeValueAsString(result));
            response.getWriter().flush();
            return;

        } catch (Exception e) {
            // 🔥 토큰 변조 / 잘못된 토큰
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"INVALID_TOKEN\", \"message\":\"Invalid or tampered token.\"}");
            response.getWriter().flush();
            return;
        }

        if (memberId != null) {
            System.out.println(" 인가쪽 제대로 시행된다는거지");
            System.out.println("memberId = " + memberId);

            Member userEntity = memberRepository.findByEmail(memberId);
            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            Authentication authentication =
                new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }

}
