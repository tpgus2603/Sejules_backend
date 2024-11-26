package hello.goodnews.configuration;

import hello.goodnews.auth.LoginArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginArgumentResolver loginArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000", // 웹 프론트엔드 도메인
                        "http://10.0.2.2:3000", // Android 에뮬레이터에서의 localhost
                        "http://192.168.x.x:3000", // 네트워크에 연결된 실제 디바이스에서 사용하는 IP
                        "http://your-mobile-app-domain.com" // 필요 시 모바일 앱의 도메인 또는 IP 추가
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }
}

