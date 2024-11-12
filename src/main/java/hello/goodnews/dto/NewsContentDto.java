package hello.goodnews.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class NewsContentDto {
    private String title;
    private String shortcut;
    private String content;
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private String keywordDetail1;
    private String keywordDetail2;
    private String keywordDetail3;
    private LocalDateTime published_date;

}
