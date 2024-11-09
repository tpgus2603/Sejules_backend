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

    public NewsPageResponse categorySearch(CategoryType categoryType, int page) //카테고리 타입 입력
    {
        // Pageable 설정 (페이지 크기 고정: 20)
        PageRequest pageable = PageRequest.of(page, 10);
        List<NewsDto> newsDtoList= newsRepository.findByCategoryType(categoryType,pageable).getContent();
        return new NewsPageResponse(newsDtoList);
    }

}
