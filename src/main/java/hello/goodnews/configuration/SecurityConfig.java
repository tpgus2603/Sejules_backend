package hello.goodnews.configuration;

import hello.goodnews.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Profile("!test") // 'test' 프로파일이 아닐 때만 활성화
public class SecurityConfig {

    private final LoginService loginService; // LoginService를 OAuth2UserService로 사용

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (필요 시 활성화)
                .csrf(csrf -> csrf.disable())
                // H2 콘솔을 위한 헤더 설정 비활성화 (개발용)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                // 요청에 대한 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "index.html", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/api/login").permitAll() // 공개 경로
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                // 로그아웃 설정
                .logout(logout -> logout.logoutSuccessUrl("/"))
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/login/success", true) // 로그인 성공 후 리다이렉트할 URL 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(loginService) // LoginService를 OAuth2UserService로 사용
                        )
                )
                // 세션 관리 설정
                .sessionManagement(session -> session
                        .maximumSessions(1) // 사용자당 최대 1 세션 허용
                        .expiredUrl("/session-expired") // 세션 만료 시 리다이렉트할 URL 설정
                );

        return http.build();
    }
}
