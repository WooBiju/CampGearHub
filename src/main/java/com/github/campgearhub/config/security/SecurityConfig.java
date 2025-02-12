package com.github.campgearhub.config.security;

import com.github.campgearhub.config.util.JwtTokenProvider;
import com.github.campgearhub.web.filter.JWTFilter;
import com.github.campgearhub.web.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {   // 인증 및 권한 관리 설정하는 클래스

    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    // 로그인 시에 사용자의 비밀번호를 db 에서 가져온 암호화된 비밀번호와 비교할때 사용됨
    public BCryptPasswordEncoder bCryptPasswordEncoder() {  // 비밀번호 암호화
        return new BCryptPasswordEncoder();
    }

    @Bean
    // HttpSecurity 객체 사용해서 보안 관련 설정하는 메서드
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("yourSecretKey");

        http
                .csrf((auth) -> auth.disable());    // csrf 비활성화
        http
                .formLogin((auth) -> auth.disable());   // 기본 로그인폼 비활성화
        http
                .httpBasic((auth) -> auth.disable());   // http 기본 인증 비활성화
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/join","/check-email","/login/kakao").permitAll()  // 모든 사용자
                        .anyRequest().authenticated());  // 인증된 사용자
        http
                .addFilterBefore(new JWTFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class);

        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtTokenProvider);
        loginFilter.setFilterProcessesUrl("/login");   // 로그인 엔드포인트 지정 (동작보장)
        http.addFilterAt(loginFilter,UsernamePasswordAuthenticationFilter.class);  // 로그인 커스텀 필터 추가

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // jwt 토큰 사용시 세션 무상태로 변경해줘야 함
        return http.build();
    }
}
