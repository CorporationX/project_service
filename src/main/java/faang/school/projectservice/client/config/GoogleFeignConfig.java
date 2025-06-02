package faang.school.projectservice.client.config;

import faang.school.projectservice.client.decoder.FeignErrorDecoder;
import faang.school.projectservice.client.interceptor.FeignUserInterceptor;
import faang.school.projectservice.config.context.UserContext;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleFeignConfig {
    @Value("${google.api.token}")
    private String accessToken;

    @Bean
    public RequestInterceptor feignTokenInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", String.format("Bearer %s", accessToken));
    }

    @Bean
    public FeignUserInterceptor feignGoogleUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}