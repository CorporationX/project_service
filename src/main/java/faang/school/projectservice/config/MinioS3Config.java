package faang.school.projectservice.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "false")
public class MinioS3Config {

    @Bean
    public AmazonS3 amazonS3(
            @org.springframework.beans.factory.annotation.Value("${services.s3.accessKey}") String accessKey,
            @org.springframework.beans.factory.annotation.Value("${services.s3.secretKey}") String secretKey,
            @org.springframework.beans.factory.annotation.Value("${services.s3.endpoint}") String endpoint,
            @org.springframework.beans.factory.annotation.Value("${services.s3.region:us-east-1}") String region
    ) {
        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(endpoint, region);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfig)
                .withPathStyleAccessEnabled(true) // важно для MinIO
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();
    }
}