package hello.goodnews.controller;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.dto.NewsContentDto;
import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.service.CategoryService;
import hello.goodnews.service.NewsService;
import hello.goodnews.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final NewsService newsService;
    @GetMapping("/{page}")
    public ResponseEntity<NewsPageResponse> searchCategory(@PathVariable("page") int page,
            @NotBlank(message = "카테고리 타입을 입력하세요") @RequestParam("category") CategoryType categoryType)
    {
        NewsPageResponse newsPageResponse = categoryService.categorySearch(categoryType, page);
        return ResponseEntity.ok(newsPageResponse);
    }
    @GetMapping("/content/{newsId}") //카테고리 요약 화면에서 기사 본뮨 화면으로 이동
    public ResponseEntity<NewsContentDto>searchContent(@PathVariable("newsId")Long newsId)
    {
        return ResponseEntity.ok(newsService.getNewsContent(newsId));
    }

}
