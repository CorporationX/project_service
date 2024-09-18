package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project API")
                        .version("1.0.0")
                        .description("API для управления проектами в системе CorporationX")
                        .contact(new Contact()
                                .name("CorporationX Support")
                                .email("support@CorporationX")));
    }
}
