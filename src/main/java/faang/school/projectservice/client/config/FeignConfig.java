package faang.school.projectservice.client.config;

import faang.school.projectservice.client.interceptor.FeignUserInterceptor;
import faang.school.projectservice.config.context.UserContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }
}