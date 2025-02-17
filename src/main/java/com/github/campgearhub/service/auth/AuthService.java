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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KaKaoUtil kaKaoUtil;


    public User oauthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kaKaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile profile = kaKaoUtil.requestProfile(oAuthToken);

        String email = profile.getKakao_account().getEmail();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(profile));

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

    private User createNewUser(KakaoDTO.KakaoProfile profile) {
        User newUser = AuthConverter.toUser(
                profile.getKakao_account().getEmail(),
                Role.ROLE_USER,
                null,
                profile.getKakao_account().getProfile().getNickname(),
                passwordEncoder
        );
        return userRepository.save(newUser);
    }
}
