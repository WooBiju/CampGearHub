package com.github.campgearhub.web.dto.User;

import com.github.campgearhub.data.entity.User.User;

public class UserConverter {
    public static UserResponseDTO.JoinResultDTO convertToDTO(User user) {
        return UserResponseDTO.JoinResultDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
