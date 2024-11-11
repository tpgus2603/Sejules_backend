package hello.goodnews.controller;

import hello.goodnews.auth.LoginUser;
import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.User;
import hello.goodnews.domain.UserCategory;
import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.service.CategoryService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final CategoryService categoryService;
    @GetMapping
    public Set<CategoryType> home(@LoginUser User user) {
        // User의 UserCategory들에서 categoryType만 추출하여 Set으로 변환
        return user.getUserCategories().stream()
                .map(UserCategory::getCategoryType) // UserCategory 객체에서 categoryType 추출
                .collect(Collectors.toSet());
    }
    @GetMapping("/{page}")
    public ResponseEntity<NewsPageResponse> searchCategory(@PathVariable("page") int page,
                                                           @NotBlank @RequestParam("category") CategoryType categoryType,
                                                           @LoginUser User user) {
        Set<UserCategory> userCategories = user.getUserCategories();
        if (!userCategories.contains(categoryType)) {
            throw new RuntimeException("유저가 선택하지 않은 카테고리");
        }

        NewsPageResponse newsPageResponse = categoryService.categorySearch(categoryType, page);
        return ResponseEntity.ok(newsPageResponse);
    }

    // @ExceptionHandler를 사용하여 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}