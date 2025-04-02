package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Project Service API",
                version = "1.0",
                description = "Документация для Project Service"
        )
)
public class OpenApiConfig {
}
