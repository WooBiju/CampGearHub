package com.github.campgearhub.service.User;

import com.github.campgearhub.data.entity.User.User;
import com.github.campgearhub.data.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Spring Security 에서 사용자 정보를 가져오는 서비스
// 1. DB 에서 사용자 정보를 조회
// 2. 조회한 정보를 CustomUserDetails 객체로 변환
// 3. Spring Security 가 로그인 시 사용자 정보를 검증할때 사용
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userData = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        if (userData != null) {
            // UserDetails 에 담아서 return 하면 AuthenticationManager 가 검증 함
            return new CustomUserDetails(userData);
        }

        return null;
    }

    // 회원가입 시 사용할 메서드 (비밀번호 암호화 후 저장)
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
