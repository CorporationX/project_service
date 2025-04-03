package faang.school.projectservice.config;

import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.service.JiraConfigService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
public class JiraConfig {

    @Bean
    @RequestScope
    public WebClient jiraWebClient(JiraConfigService configService) {
        JiraProperties config = configService.getJiraConfig();
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((config.getEmail() + ":" + config.getApiToken()).getBytes());

        return WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("Authorization", authHeader)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
