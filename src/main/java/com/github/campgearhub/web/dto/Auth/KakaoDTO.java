package com.github.campgearhub.web.dto.Auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class KakaoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("properties")
    private KakaoProperties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)  // 정의되지 않은 필드 무시
    public static class OAuthToken {
        public String access_token;
        public String token_type;
        public String refresh_token;
        public int expires_in;
        public String scope;

    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoProperties {
        @JsonProperty("nickname")
        private String nickname;
        @JsonProperty("profile_image")
        private String profileImage;
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        @JsonProperty("profile")
        private KakaoProfile profile;
        @JsonProperty("email")
        private String email;

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class KakaoProfile {
            @JsonProperty("nickname")
            private String nickname;
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;
        }
    }



}

