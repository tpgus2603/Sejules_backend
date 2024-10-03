package hello.goodnews.controller;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.News;
import hello.goodnews.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SearchController 통합 테스트 클래스
 */
@SpringBootTest
//@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test") // 'test' 프로파일 활성화
@Transactional // 각 테스트 후 롤백하여 데이터 정리
@WithMockUser(username = "testuser", roles = {"USER"}) // 클래스 레벨에 MockUser 설
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsRepository newsRepository;

    /**
     * 테스트 데이터 삽입
     */
//    @MockBean
//    private ClientRegistrationRepository clientRegistrationRepository; // 모킹된 빈 추가
    @BeforeEach
    public void setUp() {
        newsRepository.deleteAll(); // 기존 데이터 정리

        News news1 = new News();
        news1.setTitle("Spring Boot Guide");
        news1.setContent("Content of Spring Boot Guide");
        news1.setUrl("http://example.com/1");
        news1.setPublished_date(LocalDateTime.now().minusDays(1));
        news1.setCategoryType(CategoryType.TECH);
        news1.setShortcut("SBU");
        news1.setCreated_at(LocalDateTime.now().minusDays(1));

        News news2 = new News();
        news2.setTitle("Spring Data JPA");
        news2.setContent("Content of Spring Data JPA");
        news2.setUrl("http://example.com/2");
        news2.setPublished_date(LocalDateTime.now().minusDays(2));
        news2.setCategoryType(CategoryType.TECH);
        news2.setShortcut("COSDJ");


        newsRepository.saveAll(List.of(news1, news2));
    }

    /**
     * 유효한 검색어와 페이지 번호로 요청 시 정상적인 뉴스 목록을 반환하는지 테스트
     *
     * @throws Exception 예외 발생 시
     */
    @Test
    @DisplayName("유효한 검색어와 페이지 번호로 요청 시 정상적인 뉴스 목록을 반환하는지 테스트")
    public void testSearchEndpoint() throws Exception {
        mockMvc.perform(get("/api/search/page/{page}", 0) // 페이지 번호 0
                        .param("query", "Spring") // 검색어 "Spring"
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 상태 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON 응답 기대
                .andExpect(jsonPath("$.newsList", hasSize(2))) // newsList 크기 2
                .andExpect(jsonPath("$.newsList[0].title", is("Spring Boot Guide"))) // 첫 번째 뉴스 제목
                .andExpect(jsonPath("$.newsList[0].content", is("Content of Spring Boot Guide"))) // 첫 번째 뉴스 내용
                .andExpect(jsonPath("$.newsList[0].url", is("http://example.com/1"))) // 첫 번째 뉴스 URL
                .andExpect(jsonPath("$.newsList[1].title", is("Spring Data JPA"))) // 두 번째 뉴스 제목
                .andExpect(jsonPath("$.newsList[1].content", is("Content of Spring Data JPA"))) // 두 번째 뉴스 내용
                .andExpect(jsonPath("$.newsList[1].url", is("http://example.com/2")));
    }

    /**
     * 빈 검색어로 요청 시 400 Bad Request와 함께 에러 메시지를 반환하는지 테스트
     *
     * @throws Exception 예외 발생 시
     */
    @Test
    @DisplayName("빈 검색어로 요청 시 400 Bad Request와 함께 에러 메시지를 반환하는지 테스트")
    public void testSearchWithEmptyQuery() throws Exception {
        mockMvc.perform(get("/api/search/page/{page}", 0) // 페이지 번호 0
                        .param("query", "") // 빈 검색어
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 상태 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON 응답 기대
                .andExpect(jsonPath("$.errors", hasSize(1))) // errors 배열 크기 1
                .andExpect(jsonPath("$.errors[0].field", is("query"))) // 오류 필드
                .andExpect(jsonPath("$.errors[0].message", is("검색어는 비어 있을 수 없습니다."))); // 오류 메시지 확인
    }

    /**
     * 너무 긴 검색어로 요청 시 400 Bad Request와 함께 에러 메시지를 반환하는지 테스트
     *
     * @throws Exception 예외 발생 시
     */
    @Test
    @DisplayName("너무 긴 검색어로 요청 시 400 Bad Request와 함께 에러 메시지를 반환하는지 테스트")
    public void testSearchWithLongQuery() throws Exception {
        // 너무 긴 검색어 생성 (101자)
        String longQuery = "a".repeat(101);

        mockMvc.perform(get("/api/search/page/{page}", 0) // 페이지 번호 0
                        .param("query", longQuery) // 너무 긴 검색어
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 상태 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON 응답 기대
                .andExpect(jsonPath("$.errors", hasSize(1))) // errors 배열 크기 1
                .andExpect(jsonPath("$.errors[0].field", is("query"))) // 오류 필드
                .andExpect(jsonPath("$.errors[0].message", is("검색어는 100자 이내로 입력해주세요."))); // 오류 메시지 확인
    }

    /**
     * 음수 페이지 번호로 요청 시 400 Bad Request와 함께 에러 메시지를 반환하는지 테스트
     *
     * @throws Exception 예외 발생 시
     */
    @Test
    @DisplayName("음수 페이지 번호로 요청 시 400 Bad Request와 함께 에러 메시지를 반환하는지 테스트")
    public void testSearchWithNegativePage() throws Exception {
        mockMvc.perform(get("/api/search/page/{page}", -1) // 음수 페이지 번호
                        .param("query", "Spring")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 상태 기대
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // JSON 응답 기대
                .andExpect(jsonPath("$.error", is("페이지 번호는 0 이상이어야 합니다."))); // 에러 메시지 확인
    }
}