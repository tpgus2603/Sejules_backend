package hello.goodnews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NewsPageResponse {
    private List<NewsDto> newsList;
}