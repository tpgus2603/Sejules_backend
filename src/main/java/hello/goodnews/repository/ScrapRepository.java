package hello.goodnews.repository;

import hello.goodnews.domain.News;
import hello.goodnews.domain.Scrap;
import hello.goodnews.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findByUser(User user);
    List<Scrap> findByNews(News news);
}
