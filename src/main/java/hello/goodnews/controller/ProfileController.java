package hello.goodnews.controller;

import hello.goodnews.auth.LoginUser;
import hello.goodnews.domain.User;
import hello.goodnews.dto.UserProfileDTO;
import hello.goodnews.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController { //세션에 있는 사용자에 대해 카테고리 정보랑 성별 입력시켜줌

    private final UserService userService;

    /**
     * 사용자 추가 정보 업데이트 엔드포인트
     * @param userProfileDTO 사용자로부터 입력받은 추가 정보
     * @param bindingResult 바인딩 결과
     * @param user 현재 로그인된 사용자
     * @return 성공 또는 오류 메시지
     */
    @PostMapping("/complete")
    public ResponseEntity<?> completeUserProfile(
            @Valid @RequestBody UserProfileDTO userProfileDTO,
            BindingResult bindingResult,
            @LoginUser User user) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("입력값 오류", bindingResult.getAllErrors()));
        }

        try {
            userService.updateUserProfile(user, userProfileDTO);
            return ResponseEntity.ok().body(new SuccessResponse("프로필이 성공적으로 업데이트되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("프로필 업데이트 실패", e.getMessage()));
        }
    }

    // 응답용 DTO 클래스들
    @Data
    @AllArgsConstructor
    static class SuccessResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String error;
        private Object details;
    }
}
