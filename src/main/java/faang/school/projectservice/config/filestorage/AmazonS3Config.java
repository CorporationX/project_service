package faang.school.projectservice.config.filestorage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {

    private final AwsProperties awsProperties;

    @Bean
    public AmazonS3 amazonS3() {
        String accessKey = awsProperties.getAccessKey();
        String secretKey = awsProperties.getSecretKey();
        String endpoint = awsProperties.getEndpoint();
        String bucketName = awsProperties.getBucketName();

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .enablePathStyleAccess()
                .build();

        if (!s3Client.doesBucketExistV2(bucketName)) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            Bucket bucket = s3Client.createBucket(createBucketRequest);
            log.info("Created bucket: {}", bucket.getName());
        }
        return s3Client;
    }
}
