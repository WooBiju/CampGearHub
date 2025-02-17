package com.github.campgearhub.web.controller.User;

import com.github.campgearhub.BaseResponse;
import com.github.campgearhub.data.entity.User.User;
import com.github.campgearhub.service.User.UserService;
import com.github.campgearhub.service.auth.AuthService;
import com.github.campgearhub.web.dto.User.JoinRequestDTO;
import com.github.campgearhub.web.dto.User.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // 이메일 중복 검증
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        boolean isDuplicate = userService.checkEmail(email);
        if (isDuplicate) {
            return ResponseEntity.badRequest().body("이미 사용중인 이메일입니다.");
        }
        return ResponseEntity.ok("사용 가능한 이메일 입니다.");
    }


    // 회원가입
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createUser(
            @RequestPart("user") JoinRequestDTO joinRequestDTO,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
            userService.createUser(joinRequestDTO, profileImage);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // spring Security 의 LoginFilter 가 /login 요청을 가로채서 로그인 처리를 하고 있음 -> 컨트롤러 x

     // OAuth2 로그인 구현 (카카오)
    @GetMapping("/login/kakao")
    public BaseResponse<UserResponseDTO.JoinResultDTO> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {

        log.info("Received Kakao Authorization Code: {}", accessCode);
        // OAuth 로그인 후 , user 객체 반환
        User user = authService.oauthLogin(accessCode,httpServletResponse);

        // 응답 DTO 반환
        return BaseResponse.onSuccess(UserResponseDTO.JoinResultDTO.from(user));
    }
}
