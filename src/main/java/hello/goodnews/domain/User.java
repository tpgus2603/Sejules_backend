package hello.goodnews.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Gender 필드를 Enum 타입으로 변경
    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;
    @Column
    private LocalDateTime created_at = LocalDateTime.now();

    // 사용자 스크랩 (일대다 관계)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Scrap> scraps = new HashSet<>();

    // 사용자 카테고리 선택 (일대다 관계)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<UserCategory> userCategories = new HashSet<>();

    @Builder
    User(Long id, String name,String email,Gender gender,Set<UserCategory> userCategories)
    {
        this.id=id;
        this.name=name;
        this.email=email;
        this.gender=gender;
        this.userCategories=userCategories;
    }

}