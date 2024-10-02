package hello.goodnews.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewsDto {
    private String title;
    private String content;
    private String url;
}
