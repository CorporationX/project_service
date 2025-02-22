package faang.school.projectservice.client.jira;

import faang.school.projectservice.properties.JiraProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class JiraRestClientConfig {

    private final JiraProperties jiraProperties;

    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .baseUrl(jiraProperties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, jiraProperties.getToken())
                .requestInterceptors(interceptors ->
                        interceptors.add(basicAuthInterceptor(jiraProperties.getUsername(), jiraProperties.getToken())))
                .build();
    }

    private ClientHttpRequestInterceptor basicAuthInterceptor(String username, String password) {
        return (request, body, execution) -> {
            String authHeader = "Basic " + Base64.getEncoder()
                    .encodeToString((String.format("%s:%s", username, password)).getBytes(StandardCharsets.UTF_8));
            request.getHeaders().set(HttpHeaders.AUTHORIZATION, authHeader);
            return execution.execute(request, body);
        };
    }
}
