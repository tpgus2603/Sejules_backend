package hello.goodnews.controller;



import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.service.SearchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 검색 API 컨트롤러
 */
/**
 * 검색 API 컨트롤러
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 검색 API 엔드포인트
     * @param page         페이지 번호
     * @param searchQuery  검색어 (쿼리 파라미터)
     * @return 검색된 News 리스트와 페이징 정보
     */
    @GetMapping("/{page}")
    public ResponseEntity<NewsPageResponse> search(
            @PathVariable("page") int page,
            @RequestParam("query") @Valid @Size(max = 100, message = "검색어는 100자 이내로 입력해주세요.")
            @NotBlank(message = "검색어는 비어 있을 수 없습니다.") String searchQuery) {
        NewsPageResponse response = searchService.searchNews(
                searchQuery,
                page
        );
        return ResponseEntity.ok(response);
    }

}
