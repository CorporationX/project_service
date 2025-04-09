package faang.school.projectservice;

import faang.school.projectservice.config.s3.S3Properties;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableConfigurationProperties({S3Properties.class})
@EnableJpaAuditing
@ConfigurationPropertiesScan
@Async
public class ProjectServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
