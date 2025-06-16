package faang.school.projectservice.config.jira;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JiraConfig {

    private final JiraProperties jiraProperties;

    @Bean
    public RestTemplate jiraRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            String auth = jiraProperties.username() + ":" + jiraProperties.apiToken();
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            String authHeader = "Basic " + new String(encodedAuth);
            log.debug("Authorization header: {}", authHeader);
            request.getHeaders().set("Authorization", authHeader);
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
