package com.littlehikerfriends.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {
    
    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록
     * BCrypt는 강력한 해시 함수로 비밀번호 보안에 널리 사용됨
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
