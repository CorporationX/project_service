package faang.school.projectservice.config;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        Resource resource = new ClassPathResource("openapi.yaml");
        try (InputStream inputStream = resource.getInputStream()) {
            return Yaml.mapper().readValue(inputStream, OpenAPI.class);
        }
    }
}
