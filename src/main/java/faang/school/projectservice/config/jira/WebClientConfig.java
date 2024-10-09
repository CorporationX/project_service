package faang.school.projectservice.config.jira;

import faang.school.projectservice.dto.client.UserDto;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    public WebClient jiraWebClient(String username, String token, String projectUrl) {
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + token).getBytes());

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(20))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000);

        return WebClient.builder()
                .baseUrl(projectUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
