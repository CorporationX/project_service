package faang.school.projectservice.config;

import faang.school.projectservice.validator.FileValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FileValidator fileValidator(ProjectProperties projectProperties) {
        long maxFileSize = projectProperties.getMaxFileSizeInBytes();
        return new FileValidator(maxFileSize);
    }
}
