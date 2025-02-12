package com.github.campgearhub.web.dto.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWTToken {
    private String accessToken;
    private String refreshToken;
}
