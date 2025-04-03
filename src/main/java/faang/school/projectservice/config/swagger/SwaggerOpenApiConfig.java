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
<<<<<<<< HEAD:src/main/java/faang/school/projectservice/config/swagger/SwaggerOpenApiConfig.java
public class SwaggerOpenApiConfig {
========
public class OpenApiSwaggerConfig {
>>>>>>>> 58a300de1 (BSJ-65404-donations-cross-review):src/main/java/faang/school/projectservice/config/swagger/OpenApiSwaggerConfig.java
}
