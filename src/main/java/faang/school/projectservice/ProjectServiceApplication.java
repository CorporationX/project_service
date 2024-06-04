package faang.school.projectservice;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ProjectServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Project Service API")
                        .description("Project service API for Corporation X")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("link to github Corporation X")
                        .url("https://github.com/CorporationX/"));
    }
}