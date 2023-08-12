package faang.school.projectservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@OpenAPIDefinition(
        info = @Info(
                title = "Project Service",
                version = "1.0.0",
                description = "OpenApi documentation for Project Service",
                contact = @Contact(
                        name = "Faang School",
                        url = "https://faang.school"
                )
        )
)
public class ProjectServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
