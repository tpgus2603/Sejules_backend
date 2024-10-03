package hello.goodnews.service;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.News;
import hello.goodnews.domain.Scrap;
import hello.goodnews.domain.User;
import hello.goodnews.dto.NewsDto;
import hello.goodnews.repository.NewsRepository;
import hello.goodnews.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final NewsRepository newsRepository;


   //스크랩기능
    @Transactional
    public void scrapNews(User user, Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id " + newsId));

        // 중복 스크랩 방지
        scrapRepository.findByUserAndNews(user, news).ifPresent(s -> {
            throw new DuplicateScrapException("News already scrapped");
        });
        Scrap scrap = new Scrap();
        scrap.setUser(user);
        scrap.setNews(news);
        scrap.setScraped_date(LocalDateTime.now());

        scrapRepository.save(scrap);
    }

    /**
     * 스크랩한 뉴스 조회하기
     * @param user 현재 사용자
     * @param categoryType 선택된 카테고리 (선택 사항)
     * @return 스크랩한 뉴스 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<NewsDto> getScrappedNews(User user, CategoryType categoryType) {
        List<Scrap> scraps;
        if (categoryType != null) {
            scraps = scrapRepository.findByCategory(user, categoryType);
        } else {
            scraps = scrapRepository.findByUser(user);
        }

        // 불필요한 필드를 제거하고 NewsDto로 변환
        return scraps.stream()
                .map(s -> new NewsDto(
                        s.getNews().getId(),
                        s.getNews().getTitle(),
                        s.getNews().getContent(),
                        s.getNews().getUrl(),
                        s.getNews().getPublished_date()
                ))
                .collect(Collectors.toList());
    }
}