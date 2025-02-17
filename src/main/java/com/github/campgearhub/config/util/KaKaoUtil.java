package com.github.campgearhub.config.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.campgearhub.web.dto.Auth.KakaoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class KaKaoUtil {

    @Value("${spring.kakao.auth.clientId}")
    private String clientId;

    @Value("${spring.kakao.auth.redirectUri}")
    private String redirectUri;

    // 카카오 인가코드로 Token 받아오는 메서드
    public KakaoDTO.OAuthToken requestToken(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("redirect_uri", redirectUri);
        map.add("code", accessToken);

        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // response 로 받은것을 ObjectMapper 를 통해 해당 DTO 클래스로 담기
        ObjectMapper mapper = new ObjectMapper();
        KakaoDTO.OAuthToken token = null;

        try {
            token = mapper.readValue(response.getBody(),KakaoDTO.OAuthToken.class);
            log.info("oAuthToken : " + token.getAccess_token());
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    public KakaoDTO requestProfile(KakaoDTO.OAuthToken token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Authorization", "Bearer " + token.getAccess_token());

        HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        KakaoDTO kakaoDTO = null;

        try {
            kakaoDTO = mapper.readValue(response.getBody(),KakaoDTO.class);
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return kakaoDTO;
    }
}
