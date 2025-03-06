package faang.school.projectservice.config;

import faang.school.projectservice.properties.ProjectServiceProperties;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class JiraConfig {

    private final ProjectServiceProperties properties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String auth = properties.getJira().getUsername() + ":" + properties.getJira().getToken();
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            template.header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
            template.header(HttpHeaders.CONTENT_TYPE, "application/json");
        };
    }
}