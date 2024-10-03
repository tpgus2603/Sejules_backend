package hello.goodnews.repository;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.News;
import hello.goodnews.domain.Scrap;
import hello.goodnews.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    //유저가 스크랩한 뉴스이름으로 스크랩정보 가져오기
    Optional<Scrap> findByUserAndNews(User user, News news);

    //모든 유저의 스크랩 가져오기
    List<Scrap> findByUser(User user);

    //  특정 사용자와 카테고리에 해당하는 스크랩된 뉴스 조회

    @Query("SELECT s FROM Scrap s WHERE s.user = :user AND s.news.categoryType = :categoryType")
    List<Scrap> findByCategory(@Param("user") User user, @Param("categoryType") CategoryType categoryType);
}
