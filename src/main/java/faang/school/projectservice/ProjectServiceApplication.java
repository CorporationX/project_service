package faang.school.projectservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(title = "Project Service API", version = "1.0", description = "API for managing projects and vacancies")
)
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ProjectServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
