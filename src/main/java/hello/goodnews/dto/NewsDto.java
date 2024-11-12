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
public class NewsDto {
    private Long id;
    private String title;
    private String keyword1;
    private String keyword2;
    private String keyword3;

    private String shortcut1;
    private String shortcut2;
    private String shortcut3;
    private LocalDateTime published_date;
}
