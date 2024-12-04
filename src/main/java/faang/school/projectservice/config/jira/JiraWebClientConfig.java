package faang.school.projectservice.config.jira;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class JiraWebClientConfig {

    @Value("${jira.username}")
    private String email;

    @Value("${jira.token}")
    private String token;

    @Value("${jira.url}")
    private String baseUrl;

    @Bean
    public WebClient jiraWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(this::configureHeaders)
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .responseTimeout(Duration.ofSeconds(5))
                ))
                .build();
    }

    private void configureHeaders(HttpHeaders headers) {
        String auth = String.format("%s:%s", email, token);

        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = String.format("Basic %s", new String(encodedAuth));
        headers.set(org.springframework.http.HttpHeaders.AUTHORIZATION, authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }
}
