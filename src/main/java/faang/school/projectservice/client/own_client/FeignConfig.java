package faang.school.projectservice.client.own_client;

import feign.Client;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("Authorization", "Bearer token");
    }

    @Bean
    public Client feignClient() {
        return null; //new OkHttpClient(); // Использовать OkHttp вместо стандартного
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}*/