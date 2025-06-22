package faang.school.projectservice.config.minio;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class S3Config {
    private static final int TEN_SECONDS = 10_000;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.isMocked}")
    private boolean isMocked;

    @Bean
    public AmazonS3 amazonS3Client() {
        if (isMocked) {
            throw new IllegalStateException("S3 client is mocked, bean is not available.");
        }

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        AwsClientBuilder.EndpointConfiguration endpointConfig =
                new AwsClientBuilder.EndpointConfiguration(endpoint, "us-east-1");

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setConnectionTimeout(TEN_SECONDS);
        clientConfig.setSocketTimeout(TEN_SECONDS);

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpointConfig)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Bean
    public String s3BucketName() {
        return this.bucketName;
    }
}
