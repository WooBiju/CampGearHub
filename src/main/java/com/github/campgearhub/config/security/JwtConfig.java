package com.github.campgearhub.config.security;

import com.github.campgearhub.config.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(@Value("${jwt.secret-key-source}") String secret) {
        return new JwtTokenProvider(secret);

    }

}
