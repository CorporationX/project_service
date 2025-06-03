package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class S3Config {
    @Value("${services.s3.accessKey}")
    private String s3AccessKeyId;
    @Value("${services.s3.secretKey}")
    private String s3SecretAccessKey;
    @Value("${services.s3.region}")
    private String region;
    @Value("${services.s3.endpoint:}")
    private String endpoint;

    @Bean
    public AmazonS3 amazonS3() {
        log.info("Configuring Amazon S3 client with access key: {}", s3AccessKeyId);
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3AccessKeyId, s3SecretAccessKey);

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials));
        if (endpoint != null && !endpoint.isEmpty()) {
            log.info("Using custom S3 endpoint: {}", endpoint);
            AwsClientBuilder.EndpointConfiguration endpointConfig =
                    new AwsClientBuilder.EndpointConfiguration(endpoint, region);
            builder.withEndpointConfiguration(endpointConfig)
                    .withPathStyleAccessEnabled(true);
        } else {
            log.info("Using AWS S3 default endpoint for region: {}", region);
            builder.withRegion(region);
        }
        return builder.build();
    }
}
