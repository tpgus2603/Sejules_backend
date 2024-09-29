package hello.goodnews.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.goodnews.auth.LoginArgumentResolver;
import hello.goodnews.domain.CategoryType;
import hello.goodnews.domain.User;
import hello.goodnews.dto.UserProfileDTO;
import hello.goodnews.repository.UserRepository;
import hello.goodnews.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfileController.class)
@Import(LoginArgumentResolver.class) // 커스텀 Argument Resolver 등록
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // LoginArgumentResolver에서 사용됨

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser;

    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        // 모킹할 사용자 설정
        mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .gender(null)
                .userCategories(Set.of())
                .build();

        // 세션 설정
        mockSession = new MockHttpSession();
        mockSession.setAttribute("userId", 1L);
    }

    @Test
    @WithMockUser(username = "john@example.com") // 인증된 사용자로 시뮬레이션 컨트롤러 로직 검증
    void completeUserProfile_Success() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("MALE");
        dto.setCategories(Set.of(CategoryType.POLITICS, CategoryType.ECONOMY, CategoryType.TECH, CategoryType.ENVIRONMENT));

        // UserService 모킹
        doNothing().when(userService).updateUserProfile(any(User.class), any(UserProfileDTO.class));

        mockMvc.perform(post("/api/profile/complete")
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession)) // 세션 포함
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("프로필이 성공적으로 업데이트되었습니다."));

        // 서비스 메서드 호출 검증
        verify(userService, times(1)).updateUserProfile(any(User.class), any(UserProfileDTO.class));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_InvalidInput_MissingGender() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender(null); // 성별 누락
        dto.setCategories(Set.of(CategoryType.POLITICS, CategoryType.ECONOMY, CategoryType.TECH, CategoryType.ENVIRONMENT));

        mockMvc.perform(post("/api/profile/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("입력값 오류"));

        // 서비스 메서드가 호출되지 않았는지 검증
        verify(userService, times(0)).updateUserProfile(any(User.class), any(UserProfileDTO.class));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_InvalidInput_CategoryCount() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("FEMALE");
        dto.setCategories(Set.of(CategoryType.POLITICS, CategoryType.ECONOMY)); // 2개 카테고리

        mockMvc.perform(post("/api/profile/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("입력값 오류"));
        verify(userService, times(0)).updateUserProfile(any(User.class), any(UserProfileDTO.class));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void completeUserProfile_ServiceException() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("INVALID_GENDER");
        dto.setCategories(Set.of(CategoryType.POLITICS, CategoryType.ECONOMY, CategoryType.TECH, CategoryType.ENVIRONMENT));

        doThrow(new IllegalArgumentException("not vaild gender."))
                .when(userService).updateUserProfile(any(User.class), any(UserProfileDTO.class));

        MvcResult result = mockMvc.perform(post("/api/profile/complete")
                        .with(csrf()) // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .session(mockSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("프로필 업데이트 실패"))
                .andExpect(jsonPath("$.details").value("not vaild gender."))
                .andReturn();
        System.out.println("result = " + result.getResponse().getContentAsString());

        verify(userService, times(1)).updateUserProfile(any(User.class), any(UserProfileDTO.class));
    }
    @Test
    @WithMockUser(username = "john@example.com", roles = {"USER"})
    void completeUserProfile_servcie() throws Exception {
        // DTO 객체 생성
        UserProfileDTO dto = new UserProfileDTO();
        dto.setGender("FEMALE");
        dto.setCategories(Set.of(CategoryType.POLITICS, CategoryType.ECONOMY, CategoryType.TECH, CategoryType.ENVIRONMENT));
        // Mocking된 User 객체 생성
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        // UserRepository에서 사용자를 찾아서 리턴하도록 Mock 설정
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // POST 요청 실행
        mockMvc.perform(post("/api/profile/complete")
                        .with(csrf())  // CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .session(mockSession))
                .andExpect(status().isOk())  // 성공적으로 업데이트되었는지 확인
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));

        // UserService의 updateUserProfile 메서드가 올바르게 호출되었는지 검증
        verify(userService, times(1)).updateUserProfile(any(User.class), eq(dto));
    }
}