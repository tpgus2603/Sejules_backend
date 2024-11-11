package hello.goodnews.service;

import hello.goodnews.domain.News;
import hello.goodnews.dto.NewsContentDto;
import hello.goodnews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsContentDto getNewsContent(Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new RuntimeException("해당 뉴스가 존재하지 않습니다."));
        return new NewsContentDto(
                news.getTitle(),
                news.getShortcut(),
                news.getContent(),
                news.getShortcut1(),
                news.getShortcut2(),
                news.getShortcut3(),
                news.getPublished_date()
        );
    }
}
