package hello.goodnews.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NewsContentDto {
    private String title;
    private String content;
    private LocalDateTime published_date;

}
