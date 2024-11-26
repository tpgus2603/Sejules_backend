package hello.goodnews.service;

import hello.goodnews.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;


@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        // 역할(Role)이 없으므로 빈 컬렉션을 사용
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                Collections.emptyList()
        );
    }

    @Override
    public String getPassword() {
        return ""; // 비밀번호가 없으므로 빈 문자열 반환
    }

    @Override
    public String getUsername() {
        return email;
    }

    // 계정 관련 상태는 모두 true로 설정
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
