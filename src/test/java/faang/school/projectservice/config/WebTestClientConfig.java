package faang.school.projectservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;

@Configuration
public class WebTestClientConfig {

    @Value("${server.port}")
    private int port;

    @Value("${spring.mvc.servlet.path}")
    private String servletPath;

    @Bean
    public WebTestClient webTestClient() {
        return WebTestClient.bindToServer()
                .baseUrl(String.format("http://localhost:%d%s", port, servletPath))
                .build();
    }
}