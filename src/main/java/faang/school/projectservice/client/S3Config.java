package faang.school.projectservice.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "services.s3")
public class S3Config {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;

    @Bean()
    public AmazonS3 amazonS3() {
        AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .withPathStyleAccessEnabled(true).build();

        if (!amazonS3Client.doesBucketExistV2(bucketName)) {
            amazonS3Client.createBucket(new CreateBucketRequest(bucketName));
        }
        return amazonS3Client;
    }
}
