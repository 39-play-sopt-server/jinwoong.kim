package org.sopt.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * CLASS: 비밀번호 해시 생성과 검증에 사용할 PasswordEncoder를 등록합니다.
 */
@Configuration
public class PasswordConfig {
    /**
     * METHOD: BCryptPasswordEncoder를 빈으로 등록해 필요한 곳에서 주입받도록 합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
