package hello.goodnews.service;


import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.News;
import hello.goodnews.dto.NewsDto;
import hello.goodnews.dto.NewsPageResponse;
import hello.goodnews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final NewsRepository newsRepository;

    public NewsPageResponse categorySearch(CategoryType categoryType, int page) {
        PageRequest pageable = PageRequest.of(page, 10);
        List<News> newsList = newsRepository.findByCategoryType(categoryType, pageable).getContent();
        // News -> NewsDto 매핑
        List<NewsDto> newsDtoList = newsList.stream()
                .map(news -> NewsDto.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .keyword1(news.getKeyword1())
                        .keyword2(news.getKeyword2())
                        .keyword3(news.getKeyword3())
                        .shortcut1(news.getShortcut1())
                        .shortcut2(news.getShortcut2())
                        .shortcut3(news.getShortcut3())
                        .published_date(news.getPublished_date())
                        .build())
                .toList();
        log.info("e!");
        return new NewsPageResponse(newsDtoList);
    }
}
