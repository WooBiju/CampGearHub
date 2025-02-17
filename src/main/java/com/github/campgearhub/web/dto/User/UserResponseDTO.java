package com.github.campgearhub.web.dto.User;

import com.github.campgearhub.data.entity.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class UserResponseDTO {


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinResultDTO{
        private Integer userId;
        private String email;
        private String nickname;

        public static JoinResultDTO from(User user) {
            return new JoinResultDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getNickname()
            );
        }
    }

}
