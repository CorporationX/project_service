package faang.school.projectservice.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig {
    @Bean
    MultipartConfigElement multipartConfiguration() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.of(5, DataUnit.MEGABYTES));
        factory.setMaxRequestSize(DataSize.of(5, DataUnit.MEGABYTES));
        return factory.createMultipartConfig();
    }
}
