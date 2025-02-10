package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Service API")
                        .version("1.0.0")
                        .description("API for managing projects in the project service.")
                        .contact(new Contact()
                                .name("faang-school-basilisk8")
                                .email("faang-school-basilisk8@example.com")
                                .url("https://faang-school-basilisk8.com/.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project Service Documentation")
                        .url("https://faang-school-basilisk8-docs.com"));
    }
}
