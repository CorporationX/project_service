package faang.school.projectservice.config.jira;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JiraConfig {

    private final JiraProperties jiraProperties;

    @Bean
    public WebClient jiraWebClient() {
        String credentials = jiraProperties.username() + ":" + jiraProperties.apiToken();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedCredentials;

        log.debug("Jira base URL: {}", jiraProperties.jiraRestApiBaseUrl());
        log.debug("Authorization header: {}", authHeader);

        return WebClient.builder()
                .baseUrl(jiraProperties.jiraRestApiBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, authHeader)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}