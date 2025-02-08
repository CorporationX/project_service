package faang.school.projectservice.client;

import faang.school.projectservice.config.JiraProperties;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class JiraConfig {

    private final JiraProperties jiraProperties;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String auth = jiraProperties.getUsername() + ":" + jiraProperties.getToken();
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", "Basic " + encodedAuth);
            template.header("Content-Type", "application/json");
        };
    }
}