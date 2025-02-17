package com.github.campgearhub.web.dto.Auth;

import com.github.campgearhub.data.entity.User.Role;
import com.github.campgearhub.data.entity.User.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthConverter {

    public static User toUser(String email, Role role, String name, String password, PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .role(role)
                .password(passwordEncoder.encode(password))
                .username(name)
                .build();
    }
}
