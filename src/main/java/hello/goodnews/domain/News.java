package hello.goodnews.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="news_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String shortcut;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private LocalDateTime published_date;
    @Column(nullable = false)
    private String keyword1;

    @Column(nullable = false)
    private String keyword2;
    @Column(nullable = false)
    private String keyword3;

    @Column(nullable = false)
    private String keyword1Detail;
    @Column(nullable = false)
    private String keyword2Detail;
    @Column(nullable = false)
    private String keyword3Detail;


    // CategoryType Enum을 사용하여 카테고리 지정
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType categoryType;


    // 뉴스 스크랩 (일대다 관계)
    @OneToMany(mappedBy = "news", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Scrap> scraps = new HashSet<>();
}
