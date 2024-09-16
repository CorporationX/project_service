package faang.school.projectservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Users")
                .pathsToMatch("/users/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title("Application API")
                .version("v1")
                .description("Description")
                        .license(new License().name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .contact(new Contact().name("username")
                                .email("test@gmail.com")))
                .servers(List.of(new Server().url("http://localhost:8080")
                                .description("Dev service"),
                        new Server().url("http://localhost:8082")
                                .description("Beta service")));
    }
}
