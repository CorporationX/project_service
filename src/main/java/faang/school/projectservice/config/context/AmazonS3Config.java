package faang.school.projectservice.config.context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "services.s3.isMocked", havingValue = "true")
public class AmazonS3Config {

    @Value("${services.S3.accessKey}")
    private String accessKey;

    @Value("${service.S3.secretKey}")
    private String secretKey;

    @Value("${service.S3.bucketName}")
    private String bucketName;

    @Value("${service.S3.endpoint}")
    private String endpoint;

    @Bean(name = "client")
    public AmazonS3 amazonS3 (@Value("${services.s3.endpoint}") String endpoint,
                            @Value("${services.s3.accessKey}") String accessKey,
                            @Value("${services.s3.secretKey}") String secretKey) {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        if (!client.doesBucketExistV2(bucketName)) {
            client.createBucket(bucketName);
        }
        return client;
    }
}
