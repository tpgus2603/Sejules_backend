package hello.goodnews.auth;

import hello.goodnews.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        String token = jwtUtil.generateJwtToken(email);

        // JWT를 응답 헤더에 추가
        response.addHeader("Authorization", "Bearer " + token);

        // JSON 응답으로 토큰 반환
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().flush();
    }
}
