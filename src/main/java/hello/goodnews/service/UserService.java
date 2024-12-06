package hello.goodnews.service;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.Gender;
import hello.goodnews.domain.User;
import hello.goodnews.domain.UserCategory;
import hello.goodnews.dto.UserProfileDTO;
import hello.goodnews.repository.UserCategoryRepository;
import hello.goodnews.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserCategoryRepository userCategoryRepository;

    /**
     * 사용자의 추가 정보를 업데이트하는 메서드
     * @param user 현재 로그인된 사용자
     * @param userProfileDTO 사용자로부터 입력받은 추가 정보
     */
    @Transactional
    public void updateUserProfile(User user, UserProfileDTO userProfileDTO) {
        // 1. 성별 업데이트
        log.info("e1!");
        Gender gender = Gender.valueOf(userProfileDTO.getGender().toUpperCase());
        user.setGender(gender);
        // 2. 관심 카테고리 검증 (정확히 4개 선택했는지)
        Set<CategoryType> selectedCategories = userProfileDTO.getCategories();
        log.info("e2!");
        // 3. 기존 사용자 카테고리 삭제
        userCategoryRepository.deleteAll(user.getUserCategories());
        user.getUserCategories().clear();

        // 4. 새로운 사용자 카테고리 설정
        for (CategoryType categoryType : selectedCategories) {
            UserCategory userCategory = UserCategory.builder()
                    .user(user)
                    .categoryType(categoryType)
                    .build();
            userCategoryRepository.save(userCategory);
            user.getUserCategories().add(userCategory);
        }
        log.info("e3!");
        // 5. 사용자 엔티티 저장
        userRepository.save(user);
    }
    public User processOAuthPostLogin(String email, String name) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user.setName(name);
            userRepository.save(user);
        } else {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .build();
            userRepository.save(user);
        }
        return user;
    }

    /**
     * 사용자가 추가 정보를 입력했는지 확인하는 메서드
     * @param user 사용자 엔티티
     * @return 추가 정보가 완료되었는지 여부
     */
    public boolean isUserProfileComplete(User user) {
        return user.getGender() != null && user.getUserCategories().size() == 4;
    }
}
