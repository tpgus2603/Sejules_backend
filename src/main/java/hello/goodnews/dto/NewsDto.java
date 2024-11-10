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
    private String shortcut;
    private String keyword1;
    private String keyword2;
    private String keyword3;

    private String keyword1Detail;
    private String keyword2Detail;
    private String keyword3Detail;
    private LocalDateTime published_date;
}
