package faang.school.projectservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Project Service API with Jira Integration")
                .version("1.0.0")
                .description("API для управления проектами и задачами с интеграцией Atlassian Jira")
                .contact(new Contact()
                    .name("Development Team")
                    .email("dev@company.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local development server"),
                new Server()
                    .url("https://api.company.com")
                    .description("Production server")))
            .components(new Components()
                .addSecuritySchemes("userIdHeader", new SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .in(SecurityScheme.In.HEADER)
                    .name("X-User-Id")
                    .description("User ID for Oauth operations"))
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT authentication")))
            .addSecurityItem(new SecurityRequirement().addList("userIdHeader"))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}

