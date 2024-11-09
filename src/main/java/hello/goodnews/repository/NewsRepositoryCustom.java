package hello.goodnews.repository;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.dto.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface  NewsRepositoryCustom { //쿼리 dsl부분
    /**
     * 제목의 토큰 중 하나 이상을 포함하는 뉴스 기사를 페이징하여 검색
     * tokens   검색어 토큰 리스트
     * pageable 페이징 정보
     * @return 페이징된 뉴스 DTO 리스트
     */
    Page<NewsDto> searchNewsByTitleTokens(List<String> tokens, Pageable pageable);


}
