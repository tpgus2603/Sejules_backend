package hello.goodnews.controller;

import hello.goodnews.auth.LoginUser;
import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.User;
import hello.goodnews.dto.NewsDto;
import hello.goodnews.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scraps")
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    /**
     * 뉴스 스크랩하기
     * POST /scraps/{newsId}
     */
    @PostMapping("/{newsId}")
    public ResponseEntity<?> scrapNews(@LoginUser User user, @PathVariable Long newsId) {
        scrapService.scrapNews(user, newsId);
        return ResponseEntity.ok().body("News scrapped successfully");
    }

    /**
     * 스크랩한 뉴스 조회하기 쿼리파라미터로
     * GET /scraps?category=TECH
     */
    @GetMapping
    public ResponseEntity<List<NewsDto>> getScrappedNews(
            @LoginUser User user,
            @RequestParam(value = "category", required = false) CategoryType categoryType) {
        List<NewsDto> scrappedNews = scrapService.getScrappedNews(user, categoryType);
        return ResponseEntity.ok(scrappedNews);
    }

}
