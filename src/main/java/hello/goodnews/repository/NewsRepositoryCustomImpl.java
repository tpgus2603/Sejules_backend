package hello.goodnews.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    @Override
    public Page<NewsDto> searchNewsByTitleTokens(List<String> tokens, Pageable pageable) {
        QNews news = QNews.news;

        BooleanBuilder builder = new BooleanBuilder();
        if (tokens != null && !tokens.isEmpty()) {
            BooleanBuilder titleBuilder = new BooleanBuilder();
            for (String token : tokens) {
                titleBuilder.or(news.title.containsIgnoreCase(token));
            }
            builder.and(titleBuilder);
        }

        List<NewsDto> content = queryFactory
                .select(Projections.constructor(NewsDto.class,
                        news.title,
                        news.content,
                        news.url))
                .from(news)
                .where(builder)
                .orderBy(news.created_at.desc()) // 생성일 기준 내림차순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(news.count())
                .from(news)
                .where(builder)
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> total);
    }
}
