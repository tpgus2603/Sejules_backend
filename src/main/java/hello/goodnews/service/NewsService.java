package hello.goodnews.service;

import hello.goodnews.domain.News;
import hello.goodnews.dto.NewsContentDto;
import hello.goodnews.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                news.getPublisher(),
                news.getReporter(),
                news.getShortcut(),
                news.getContent(),
                news.getKeyword1(),
                news.getKeyword2(),
                news.getKeyword3(),
                news.getKeyword1Detail(),
                news.getKeyword2Detail(),
                news.getKeyword3Detail(),
                news.getPublished_date()
        );
    }
}
