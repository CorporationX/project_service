package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Project Service",
                description = "Faang microservice",
                version = "1.0.0",
                contact = @Contact(
                        name = "Hydra-stream",
                        email = "hydra@gmail.com",
                        url = "https://hydra.team"
                )
        )
)
public class OpenApiConfig {
}
