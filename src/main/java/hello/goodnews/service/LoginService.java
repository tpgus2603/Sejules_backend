package hello.goodnews.service;

import hello.goodnews.domain.User;
import hello.goodnews.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // DefaultOAuth2UserService를 사용하여 사용자 정보 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        log.info("OAuth2 사용자 정보 로드 시작");

        // 사용자 정보 추출
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new RuntimeException("이메일 정보가 없습니다.");
        }

        // DB에서 사용자 조회 또는 생성
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 필요 시 사용자 정보 업데이트
            user.setName(name);
            userRepository.save(user);
            log.info("기존 사용자 업데이트: {}", email);
        } else {
            log.info("새 사용자 생성: {}", name);
            user = User.builder()
                    .name(name)
                    .email(email)
                    .gender(null) // 필요 시 설정
                    .build();
            userRepository.save(user);
            log.info("새 사용자 저장: {}", email);
        }

        // UserDetails에 사용자 정보를 저장하지 않으므로, attributes만 전달
        return new DefaultOAuth2User(
                Collections.emptyList(), // 역할이 없으므로 빈 리스트
                oAuth2User.getAttributes(),
                "email" // 기본 키로 사용할 속성 지정
        );
    }
}
