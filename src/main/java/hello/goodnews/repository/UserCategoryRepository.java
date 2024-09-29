package hello.goodnews.repository;

import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.User;
import hello.goodnews.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    List<UserCategory> findByUser(User user);
    boolean existsByUserAndCategoryType(User user, CategoryType categoryType);
    Optional<UserCategory> findByUserAndCategoryType(User user, CategoryType categoryType);
}