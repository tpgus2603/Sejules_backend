package hello.goodnews.controller;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.dto.NewsContentDto;
import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.service.CategoryService;
import hello.goodnews.service.NewsService;
import hello.goodnews.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final NewsService newsService;

    @GetMapping("/{page}")
    public ResponseEntity<?> searchCategory(
            @PathVariable("page") int page,
            @RequestParam("category") String category) {

        // Enum 값 검증
        CategoryType categoryType;
        try {
            categoryType = CategoryType.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid category", "지원하지 않는 카테고리입니다."));
        }

        // 서비스 호출
        NewsPageResponse newsPageResponse = categoryService.categorySearch(categoryType, page);
        return ResponseEntity.ok(newsPageResponse);
    }


    @GetMapping("/content/{newsId}")
    public ResponseEntity<?> searchContent(@PathVariable("newsId") Long newsId) {
        try {
            NewsContentDto newsContentDto = newsService.getNewsContent(newsId);
            return ResponseEntity.ok(newsContentDto);
        } catch (NoSuchElementException e) {
            log.error("News not found", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("뉴스 없음", "해당 ID에 대한 뉴스가 존재하지 않습니다."));
        } catch (Exception e) {
            log.error("Unexpected error fetching news content", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("서버 오류", "뉴스 내용을 가져오는 도중 오류가 발생했습니다."));
        }
    }

    // 응답용 DTO 클래스
    @Data
    @AllArgsConstructor
    static class ErrorResponse {
        private String error;
        private String message;
    }
}

