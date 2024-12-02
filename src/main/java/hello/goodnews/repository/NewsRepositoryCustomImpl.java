package hello.goodnews.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.QNews;
import hello.goodnews.dto.NewsDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NewsRepositoryCustomImpl implements NewsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public NewsRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 검색어 토큰을 기반으로 뉴스 기사를 페이징하여 검색
     *
     * @param tokens   검색어 토큰 리스트
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기)
     * @return 페이징된 뉴스 DTO 페이지
     */
    @Override
    public Page<NewsDto> searchNewsByTitleTokens(List<String> tokens, Pageable pageable) {
        QNews news = QNews.news; // QueryDSL이 생성한 Q 클래스 사용

        // BooleanBuilder를 사용하여 동적 쿼리 조건을 생성 (일단 검색조건이 나중에 추가될 수 있으니 2중으로)
        BooleanBuilder builder = new BooleanBuilder();
        if (tokens != null && !tokens.isEmpty()) {
            BooleanBuilder titleBuilder = new BooleanBuilder();
            for (String token : tokens) {
                // 제목에 토큰이 포함되는지 검사 (대소문자 무시)
                titleBuilder.or(news.title.containsIgnoreCase(token));
            }
            builder.and(titleBuilder);
        }

        // QueryDSL을 사용하여 뉴스 목록 조회
        List<NewsDto> content = queryFactory
                .select(Projections.constructor(NewsDto.class,
                        news.id,    // NewsDto의 title 필드에 매핑
                        news.title,
                        news.keyword1,
                        news.keyword2,
                        news.keyword3,
                        news.shortcut1,
                        news.shortcut2,
                        news.shortcut3,
                        news.published_date
                        ))    // NewsDto의 url 필드에 매핑
                .from(news)
                .where(builder)                        // 동적 조건 적용
                .orderBy(news.published_date.desc())        // 등재일 기준 내림차순 정렬
                .offset(pageable.getOffset())          // 페이징 시작 위치
                .limit(pageable.getPageSize())         // 페이지 크기 제한
                .fetch();                              // 쿼리 실행 및 결과 가져오기

        // 전체 개수 조회 (페이징 처리에 필요)
        Long total = queryFactory
                .select(news.count())
                .from(news)
                .where(builder)
                .fetchOne();

        // PageableExecutionUtils를 사용하여 Page 객체 생성
        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }
}
