package hello.goodnews.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import hello.goodnews.domain.User;
import hello.goodnews.service.UserService;
import hello.goodnews.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final GoogleIdTokenVerifier verifier;

    /**
     * 프론트엔드에서 전달된 Google ID 토큰을 처리하는 엔드포인트
     * @param googleTokenRequest 프론트엔드에서 전송된 ID 토큰
     * @return JWT 토큰 및 프로필 완성 여부
     */
    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleTokenRequest googleTokenRequest) {
        String idTokenString = googleTokenRequest.getIdToken();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                String email = idToken.getPayload().getEmail();
                String name = (String) idToken.getPayload().get("name");

                if (email == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 정보가 없습니다.");
                }

                // 사용자 조회 또는 생성
                User user = userService.processOAuthPostLogin(email, name);

                // 프로필 완성 여부 확인
                boolean isProfileComplete = userService.isUserProfileComplete(user);
                String token = jwtUtil.generateJwtToken(user.getEmail());

                // 응답 DTO 생성
                AuthResponse authResponse = new AuthResponse(isProfileComplete, token);

                return ResponseEntity.ok(authResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 ID 토큰입니다.");
            }
        } catch (GeneralSecurityException | IOException e) {
            log.error("Google ID Token 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("내부 서버 오류.");
        }
    }
    @Data
    static class GoogleTokenRequest {
        private String idToken;
    }
    @Data
    @AllArgsConstructor
    static class AuthResponse {
        private boolean isProfileComplete;
        private String token;
    }
}
