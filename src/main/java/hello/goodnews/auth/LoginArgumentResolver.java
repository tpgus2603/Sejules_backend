package hello.goodnews.auth;

import hello.goodnews.domain.User;
import hello.goodnews.repository.UserRepository;
import hello.goodnews.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @LoginUser 어노테이션이 있는 User 타입의 파라미터에 적용
        return parameter.getParameterAnnotation(LoginUser.class) != null
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("인증되지 않은 사용자");
        }

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            email = userDetails.getUsername(); // email로 설정됨
        } else {
            throw new RuntimeException("지원하지 않는 사용자 타입");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}