package hello.goodnews.controller;

import hello.goodnews.auth.LoginUser;
import hello.goodnews.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
@Slf4j
@RequiredArgsConstructor
public class LoginController {

    @GetMapping
    public String login() {
        // 구글 OAuth 로그인 페이지로 리다이렉트
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/success")
    public String loginSuccess(@LoginUser User loginUser) {
        // 로그인 성공 시 사용자 정보 처리
        log.info("loginuser={}",loginUser.getName());
        return "redirect:/home";  // 로그인 성공 후 보여줄 페이지
    }
}
