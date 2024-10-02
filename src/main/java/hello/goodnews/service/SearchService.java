package hello.goodnews.service;

import hello.goodnews.dto.NewsDto;
import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchService {

    private final NewsRepository newsRepository;

    /**
     * 검색어를 토큰으로 분리하고, 각 토큰을 포함하는 News 리스트를 페이징하여 반환
     *
     * @param searchQuery 검색어 문자열
     * @param page        페이지 번호 (0부터 시작)
     * @return 페이징된 뉴스 DTO 리스트
     */
    public NewsPageResponse searchNews(String searchQuery, int page) {

        // 화이트 스페이스를 기준으로 검색어 분리
        List<String> tokens = Arrays.asList(searchQuery.trim().split("\\s+"));

        // Pageable 설정 (페이지 크기 고정: 20)
        PageRequest pageable = PageRequest.of(page, 20);

        // 검색 수행
        List<NewsDto> newsList = newsRepository.searchNewsByTitleTokens(tokens, pageable).getContent();

        return new NewsPageResponse(newsList);
    }
}
