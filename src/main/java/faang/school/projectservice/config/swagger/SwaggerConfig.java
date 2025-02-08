package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApiInfo() {

        Info info = new Info()
                .title("Project Service API")
                .version("0.1")
                .description("Этот API предоставляет эндпойнты для управления Project Service");

        return new OpenAPI().info(info);
    }

}
