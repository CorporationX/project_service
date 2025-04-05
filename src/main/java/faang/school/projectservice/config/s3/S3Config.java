package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * Конфигурационный класс для настройки клиента Amazon S3.
 * <p>
 * Создает и настраивает бин {@link AmazonS3} для взаимодействия с объектным хранилищем.
 */
@Configuration
@RequiredArgsConstructor
public class S3Config {

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.access-key}")
    private String accessKey;

    @Value("${services.s3.secret-key}")
    private String secretKey;

    @Bean
    public AmazonS3 amazonS3() {
        Objects.requireNonNull(accessKey, "AWS access key must not be null");
        Objects.requireNonNull(secretKey, "AWS secret key must not be null");
        Objects.requireNonNull(endpoint, "AWS endpoint must not be null");

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
