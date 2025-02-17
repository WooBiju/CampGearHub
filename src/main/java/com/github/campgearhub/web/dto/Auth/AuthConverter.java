package com.github.campgearhub.web.dto.Auth;

import com.github.campgearhub.data.entity.User.Role;
import com.github.campgearhub.data.entity.User.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthConverter {

    public static User toUser(String email, Role role, String username, String nickname, String profileImage, String password) {
        return User.builder()
                .email(email)
                .role(role)
                .password(password)
                .username(username)
                .nickname(nickname)
                .profileImageUrl(profileImage)
                .build();
    }
}
