package faang.school.projectservice.client.config;

import faang.school.projectservice.client.decoder.FeignErrorDecoder;
import faang.school.projectservice.client.interceptor.FeignUserInterceptor;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.InternalServerErrorException;
import faang.school.projectservice.service.GoogleAuthService;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class GoogleFeignConfig {
    private final GoogleAuthService googleAuthServiceImpl;

    @Bean
    public RequestInterceptor feignTokenInterceptor() {
        return template -> {
            try {
                String accessToken = googleAuthServiceImpl.getAccessToken();
                template.header("Authorization", "Bearer %s".formatted(accessToken));
            } catch (IOException e) {
                throw new InternalServerErrorException("Failed to obtain Google access token for Feign request with message %s"
                        .formatted(e.getMessage()));
            }
        };
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