package com.github.campgearhub.service.User;

import com.github.campgearhub.data.entity.User.Role;
import com.github.campgearhub.data.entity.User.User;
import com.github.campgearhub.data.repository.User.UserRepository;
import com.github.campgearhub.web.dto.User.JoinRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${user.profile-image-dir}")
    private String profileImageDir;

    public boolean checkEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void createUser(JoinRequestDTO joinRequestDTO, MultipartFile profileImage) throws IOException {

        String profileImageUrl = saveProfileImage(profileImage);

        User user = new User();
        user.setEmail(joinRequestDTO.getEmail());
        user.setUsername(joinRequestDTO.getUsername());
        user.setPassword(encodePassword(joinRequestDTO.getPassword()));
        user.setNickname(joinRequestDTO.getNickname());
        user.setProfileImageUrl(profileImageUrl);
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);

    }

    // 비밀번호 암호화
    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    // 프로필 이미지 저장 메서드 (로컬)
    private String saveProfileImage(MultipartFile profileImage) throws IOException {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        String originalFilename = profileImage.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = System.currentTimeMillis() + fileExtension; // 햔재 시간 기준으로 파일명 생성

        Path path = Paths.get(profileImageDir, fileName);  // 파일 경로

        try {
            Files.createDirectories(path.getParent());  // 부모 디렉토리 생성
            profileImage.transferTo(path.toFile());     // 실제 파일을 로컬 경로에 저장

        } catch (IOException e) {
            throw new IOException(e.getMessage());

        }

        return "/image/profiles/" + fileName;

    }

}
