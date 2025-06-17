package faang.school.projectservice.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "CorporationX - Project service API",
                description = "Api for project service",
                version = "1.0.0",
                contact = @Contact(
                        name = "Kelpie stream 10"
                )
        )
)
public class OpenApiConfig {
}
