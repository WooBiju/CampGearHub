package com.github.campgearhub.config.util;

import com.github.campgearhub.web.dto.User.JWTToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
// JWT 토큰을 생성하고, 검증하여 필요한 정보를 추출하는 클래스
public class JwtTokenProvider {

    private  SecretKey secretkey;


    // 토큰의 서명을 생성하기 위한 비밀키 설정
    public JwtTokenProvider(@Value("${jwt.secret-key-source}")String secret) {
        //byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretkey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 자동으로 256비트 이상의 키를 생성해줌
    }

    // user 정보를 가지고 accessToken, RefreshToken 생성하는 메서드
    public JWTToken generateToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // AccessToken 생성
        Date expiration = new Date(now + 1000 * 60 * 60 * 24);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("authorities", authorities)
                .setExpiration(expiration)
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 1000 * 60 * 60 * 24))
                .signWith(secretkey, SignatureAlgorithm.HS256)
                .compact();

        return JWTToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    // jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("authorities") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }


        // claims 에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        // userDetails 객체를 만들어서 Authentication 반환
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorities);

    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        {
            try {
                Jwts.parser()
                        .verifyWith(secretkey)  // 서명 검증
                        .build()
                        .parseSignedClaims(token);
                return true;
            }catch (Exception e) {
                return false;
            }
        }
    }

    // 토큰에서 Claims 파싱
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretkey)
                    .build()
                    .parseClaimsJwt(accessToken)
                    .getBody();
        }catch (Exception e) {
            return null;
        }
    }

}
