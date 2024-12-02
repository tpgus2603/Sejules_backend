package hello.goodnews.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true); // Optional, logs all headers
        filter.setIncludePayload(true); // Logs request body
        filter.setMaxPayloadLength(10000); // Limit payload size
        return filter;
    }
}
