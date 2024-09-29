package hello.goodnews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.Gender;
import hello.goodnews.domain.User;
import hello.goodnews.dto.UserProfileDTO;
import hello.goodnews.repository.UserRepository;
import hello.goodnews.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProfileControllerTestV2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;
    private MockHttpSession mockSession;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @BeforeEach
    void setUp() {
        // 기존 데이터 삭제
        userRepository.deleteAll();

        // 테스트 사용자 설정 (로그인 인증 과정 생략위해 MOCKUSER 사용)
        mockUser = User.builder()
                .name("John Doe")
                .email("john@example.com")
                .gender(Gender.FEMALE)
                .userCategories(new HashSet<>()) // 불변 컬렉션 대신 HashSet 사용
                .build();

        // 데이터베이스에 사용자 저장
        mockUser = userRepository.save(mockUser);

        // 세션 설정
        mockSession = new MockHttpSession();
        mockSession.setAttribute("user", mockUser.getId());
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_Success() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("MALE");

        // 불변 컬렉션 대신 수정 가능한 HashSet 사용
        dto.setCategories(new HashSet<>(Arrays.asList(
                CategoryType.POLITICS,
                CategoryType.ECONOMY,
                CategoryType.TECH,
                CategoryType.ENVIRONMENT
        )));

        mockMvc.perform(post("/api/profile/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("프로필이 성공적으로 업데이트되었습니다."));

        // 업데이트된 사용자 검증
        User updatedUser = userRepository.findByEmail("john@example.com").orElseThrow();
        assertEquals("MALE", updatedUser.getGender().name());
        assertEquals(4, updatedUser.getUserCategories().size());
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_InvalidInput_MissingGender() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender(null); // 성별 누락

        // 불변 컬렉션 대신 수정 가능한 HashSet 사용
        dto.setCategories(new HashSet<>(Arrays.asList(
                CategoryType.POLITICS,
                CategoryType.ECONOMY,
                CategoryType.TECH,
                CategoryType.ENVIRONMENT
        )));

        mockMvc.perform(post("/api/profile/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("입력값 오류"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_InvalidInput_CategoryCount() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("FEMALE");

        // 카테고리가 2개인 경우 (4개여야 함)
        dto.setCategories(new HashSet<>(Arrays.asList(
                CategoryType.POLITICS,
                CategoryType.ECONOMY
        )));

        mockMvc.perform(post("/api/profile/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("입력값 오류"));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_ServiceException() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("INVALID_GENDER");

        // 불변 컬렉션 대신 수정 가능한 HashSet 사용
        dto.setCategories(new HashSet<>(Arrays.asList(
                CategoryType.POLITICS,
                CategoryType.ECONOMY,
                CategoryType.TECH,
                CategoryType.ENVIRONMENT
        )));

        mockMvc.perform(post("/api/profile/complete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("프로필 업데이트 실패"))
                .andExpect(jsonPath("$.details").value("유효하지 않은 성별 값입니다."));
    }
}
