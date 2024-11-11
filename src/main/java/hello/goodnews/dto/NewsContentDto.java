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
    private String shortcut;
    private String content;
    private String shortcut1;
    private String shortcut2;
    private String shortcut3;
    private LocalDateTime published_date;

}
