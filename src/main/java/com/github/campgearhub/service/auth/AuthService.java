package com.github.campgearhub.service.auth;

import com.github.campgearhub.config.util.JwtTokenProvider;
import com.github.campgearhub.config.util.KaKaoUtil;
import com.github.campgearhub.data.entity.User.Role;
import com.github.campgearhub.data.entity.User.User;
import com.github.campgearhub.data.repository.User.UserRepository;
import com.github.campgearhub.service.User.CustomUserDetails;
import com.github.campgearhub.web.dto.Auth.AuthConverter;
import com.github.campgearhub.web.dto.Auth.KakaoDTO;
import com.github.campgearhub.web.dto.User.JWTToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KaKaoUtil kaKaoUtil;


    public User oauthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kaKaoUtil.requestToken(accessCode);
        KakaoDTO kakaoDTO = kaKaoUtil.requestProfile(oAuthToken);  // 최상위 DTO 반환


        String email = kakaoDTO.getKakaoAccount().getEmail();

        String nickname;
        if (kakaoDTO.getKakaoAccount().getProfile() != null) {
            nickname = kakaoDTO.getKakaoAccount().getProfile().getNickname();
        } else if (kakaoDTO.getProperties() != null) {
            nickname = kakaoDTO.getProperties().getNickname();
        }else {
            nickname = "기본 닉네임";
        }

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(kakaoDTO,nickname));



        // customUserDetails 객체 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // CustomUserDetails -> Authentication 변환
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails,null,customUserDetails.getAuthorities()
        );

        JWTToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // jwt 를 응답 헤더에 추가
        httpServletResponse.setHeader("Authorization", jwtToken.getAccessToken());
        httpServletResponse.setHeader("Refresh-Token", jwtToken.getRefreshToken());

        return user;

    }

    private User createNewUser(KakaoDTO kakaoDTO, String nickname) {

        String email = kakaoDTO.getKakaoAccount().getEmail();

        String oauthPassword = "";

        String username = nickname;

        User newUser = AuthConverter.toUser(
                email,
                Role.ROLE_USER,
                username,
                nickname,
                kakaoDTO.getProperties().getProfileImage(),
                oauthPassword
        );
        return userRepository.save(newUser);

    }
}
