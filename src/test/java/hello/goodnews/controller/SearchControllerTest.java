package hello.goodnews.controller;

import hello.goodnews.dto.NewsDto;
import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    public void testSearch() throws Exception {
        // 테스트 데이터 설정
        NewsDto news1 = new NewsDto("Spring Boot Guide", "Content1", "http://example.com/1");
        NewsDto news2 = new NewsDto("Spring Data JPA", "Content2", "http://example.com/2");

        NewsPageResponse response = new NewsPageResponse(
                List.of(news1, news2),
                0,
                1,
                2
        );

        // 서비스 레이어 모킹
        given(searchService.searchNews(anyString(), anyInt())).willReturn(response);

        // 검색 요청 수행
        mockMvc.perform(get("/api/search/page/0")
                        .param("query", "Spring Boot")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newsList.length()").value(2))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.newsList[0].title").value("Spring Boot Guide"))
                .andExpect(jsonPath("$.newsList[0].content").value("Content1"))
                .andExpect(jsonPath("$.newsList[0].url").value("http://example.com/1"))
                .andExpect(jsonPath("$.newsList[1].title").value("Spring Data JPA"))
                .andExpect(jsonPath("$.newsList[1].content").value("Content2"))
                .andExpect(jsonPath("$.newsList[1].url").value("http://example.com/2"));
    }
}