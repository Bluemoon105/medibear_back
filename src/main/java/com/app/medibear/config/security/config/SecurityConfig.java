package com.app.medibear.config.security.config;


//import com.app.medibear.config.security.handler.OAuth2SuccessHandler;
import com.app.medibear.config.security.jwt.CustomAuthenticationFailureHandler;
import com.app.medibear.config.security.jwt.JwtAuthenticationEntryPoint;
import com.app.medibear.config.security.jwt.JwtAuthenticationFilter;
import com.app.medibear.config.security.jwt.JwtAuthorizationFilter;
//import com.app.medibear.config.security.oauth2.CustomOAuth2UserService;
import com.app.medibear.config.security.oauth2.JwtProperties;
import com.app.medibear.repository.MemberRepository;
//import com.app.medibear.utils.GetUrlToImage;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtProperties jwtProperties;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    //private final StringRedisTemplate stringRedisTemplate;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
//    private final GetUrlToImage getUrlToImage;

    //redis
    private final StringRedisTemplate stringRedisTemplate;
    //Spring Boot 3.x 이상에서는 AuthenticationManager를 직접 빌드해서 넣어줘야 필터에서 사용할 수 있음.
    //@Bean으로 분리해 두면 다른 클래스에서도 재사용 가능해서 더 좋다.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean     소셜 로그인쪽
//    public CustomOAuth2UserService customOAuth2UserService() {
//        return new CustomOAuth2UserService(memberRepository,getUrlToImage);
//    }

//    @Bean     소셜 로그인 부분
//    public OAuth2SuccessHandler oAuth2SuccessHandler() {
//        return new OAuth2SuccessHandler(jwtProperties, stringRedisTemplate);
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        // JwtAuthenticationFilter 설정  (/api/login)으로 요청이 들어오면 jwtauthenticationFilter에서 username/password검
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, stringRedisTemplate,jwtProperties);
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/login"); // <- 이거 안 하면 필터가 동작 안 함 +  클라이언트 쪽에서
        jwtAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        //로그인을 할려고 /api/login으로 요청으로 보내면 필터가 작동할수 있도록 하는 메서드이다.

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))   //react랑 연결용 cors허용
                .csrf(AbstractHttpConfigurer::disable)  //jwt방식은 세션 없기 때문에 불필요 해서 처리함
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //서버가 로그인 세션을 유지하지 않음 그래서 왜냐 토큰 방식이니까!!  그래서 stateless처리 해준다.
                .addFilter(jwtAuthenticationFilter)  // 이부분이 처음 로그인할때만 동작한다.
                .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository , jwtProperties))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401 예외처리부분 로그인 실패시
                )
                .authorizeHttpRequests(auth -> auth

                                .requestMatchers("/favicon.ico").permitAll() // ✅ favicon.ico 허용
                                .requestMatchers("/user/**").authenticated()  // 이부분은 사용자가 jwt토큰을 들고 요청하면 인증만되어있다고 확인이 된다면 통과 시키는 부분
//                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")  // 여러 권한중 하나만 있어도 허용
//                                .requestMatchers("/admin/**").hasRole("ADMIN")  //  특정 하나의 권한만 허용
//                                .requestMatchers("/api/calorie/analyze").permitAll()
                                .requestMatchers("/api/auth/refresh", "/css/**", "/js/**", "/images/**",
                                        "/join", "/api/join", "/api/non-member/**","/static/**",
                                        "/api/checkId","/api/signUp","/api/getProfileImg","/img/**","/rec/**",
                                        "/api/checkName").permitAll() // 로그인 페이지, 정적 파일은 모두 허용
                                .requestMatchers("/stress/**").permitAll()
                                .requestMatchers("/").permitAll() // 기본 홈 페이지도 허용
                                .requestMatchers("/api/calorie/**").authenticated()
//                                .requestMatchers("*").permitAll()
//                                .requestMatchers("/*").permitAll()


                        .anyRequest().authenticated() // 나머지 요청은 인증이 필요
//                                .anyRequest().permitAll()
                );
//                .oauth2Login(oauth2 -> oauth2
//                                .userInfoEndpoint(userInfo -> userInfo
//                                        .userService(customOAuth2UserService())
//                                )
//                                .successHandler(oAuth2SuccessHandler())
////                        .defaultSuccessUrl("http://localhost:3000/", true) // 로그인 성공 후 이동할 페이지

//                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://192.168.0.32:5173");
        configuration.addAllowedOrigin("https://medibear.vercel.app");
        //.allowedOriginPatterns("*")  // ✅ Spring Boot 2.4 이상에서 지원
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        // 클라이언트에서 읽을 수 있도록 Authorization 헤더 노출 추가
        configuration.addExposedHeader("Authorization");
        //refreshToken 때문에 노출해야된다.  근데 이부분은 의미 없어서 빠져도 노상관
//        configuration.addExposedHeader("Set-Cookie");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
