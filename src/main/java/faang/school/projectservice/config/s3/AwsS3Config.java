package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String accessSecretKey;

    @Value("${services.s3.region}")
    private String region;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Bean
    public AmazonS3 s3Config(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecretKey);

        AmazonS3 amazonS3Client = AmazonS3Client.builder()
                .withCredentials(
                        new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();
        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(new CreateBucketRequest(bucketName));
        }
        return amazonS3Client;
    }
}
