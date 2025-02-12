package com.github.campgearhub.web.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestDTO {
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String profilePicture;
}
