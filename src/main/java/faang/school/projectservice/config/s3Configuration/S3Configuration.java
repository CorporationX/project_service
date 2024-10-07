package faang.school.projectservice.config.s3Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import faang.school.projectservice.config.properties.PropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Configuration {

    private final PropertiesConfig config;

    @Bean
    public AmazonS3 amazonS3Client() {
        return AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.getAccessKey(),
                        config.getSecretKey())))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(config.getEndpoint(),
                        ""))
                .build();
    }
}
