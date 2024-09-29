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
    private final HttpSession httpSession;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // DefaultOAuth2UserService를 사용하여 사용자 정보 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 사용자 정보 추출
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null ) {
            throw new OAuth2AuthenticationException("no email error");
        }

        // DB에서 사용자 조회 또는 생성
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 필요 시 사용자 정보 업데이트
            user.setName(name);
            userRepository.save(user);
            log.info("Existing user updated: {}", email);
        } else {
            user = User.builder()
                    .name(name)
                    .email(email)
                    .build();
            userRepository.save(user);
            log.info("New user created: {}", email);
        }

        // 세션서장
        httpSession.setAttribute("userId", user.getId());

        // DefaultOAuth2User 반환
        // "name" 속성을 username으로 사용
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("Role_user")// "name" 속성을 username으로 사용
        ), oAuth2User.getAttributes(), "name");
    }

}
