package hello.goodnews.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "category_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_category_id")
    private Long id;

    // 사용자와의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    // 카테고리 타입을 Enum으로 저장
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false)
    private CategoryType categoryType;

    @Builder
    public UserCategory(User user,CategoryType categoryType)
    {
        this.user=user;
        this.categoryType=categoryType;
    }
}
