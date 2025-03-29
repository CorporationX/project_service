package faang.school.projectservice.service.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки клиента Amazon S3.
 * <p>
 * Создает и настраивает бин {@link AmazonS3} для взаимодействия с объектным хранилищем.
 */
@Configuration
@RequiredArgsConstructor
public class S3Configuration {

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.access-key}")
    private String accessKey;

    @Value("${services.s3.secret-key}")
    private String secretKey;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .enablePathStyleAccess()
                .build();
    }
}
