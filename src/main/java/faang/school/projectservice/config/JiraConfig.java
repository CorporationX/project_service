package faang.school.projectservice.config;

import faang.school.projectservice.dto.client.jira.JiraProperties;
import faang.school.projectservice.service.JiraConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class JiraConfig {

    private static final String HEADER_NAME = "Authorization";

    @Bean
    @RequestScope
    public WebClient jiraWebClient(JiraConfigService configService) {
        JiraProperties config = configService.getJiraConfig();

        return WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader(HEADER_NAME, "Bearer " + config.getApiToken())
                .build();
    }
}
