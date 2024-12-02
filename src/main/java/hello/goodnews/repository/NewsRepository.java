package hello.goodnews.repository;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.News;
import hello.goodnews.dto.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> ,NewsRepositoryCustom{
    Optional<News> findByUrl(String url);
     Page<News> findByCategoryType(CategoryType categoryType, Pageable pageable);
}
