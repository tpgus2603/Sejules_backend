package hello.goodnews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NewsDto {
    private Long id;
    private String title;
    private String content;
    private String url;
    private LocalDateTime published_date;
}
