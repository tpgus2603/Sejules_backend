package hello.goodnews.dto;

import hello.goodnews.domain.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



import java.util.Set;

@Data
public class UserProfileDTO {

    @NotNull(message = "성별을 선택해주세요.")
    private String gender; // "MALE", "FEMALE", "OTHER"

    @NotNull(message = "관심 카테고리 4개를 선택해주세요.")
    @Size(min = 4, max = 4, message = "정확히 4개의 카테고리를 선택해야 합니다.")
    private Set<CategoryType> categories; // 4개의 CategoryType Enum 값
}
