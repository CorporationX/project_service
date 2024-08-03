package faang.school.projectservice.config.context;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class S3Config {

    @Value("${service.s3.accessKey}")
    private String accessKey;

    @Value("${service.s3.secretKey")
    private String secretKey;

    @Value("${service.s3.endpoint")
    private String endpoint;

    @Value("${service.s3.bucketName")
    private String bucketName;

    @Bean(name = "clientAmazonS3")
    public AmazonS3 amazonS3Client() {

        AmazonS3 clientAmazonS3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                endpoint, null
                        ))
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKey, secretKey)
                        ))
                .build();
        if (!clientAmazonS3.doesBucketExistV2(bucketName)) {
            clientAmazonS3.createBucket(bucketName);
        }
        return clientAmazonS3;
    }
}
