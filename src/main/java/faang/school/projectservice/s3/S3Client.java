package faang.school.projectservice.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Client {
    @Value("${services.s3.endpoint}")
    private String endpoint;
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String secretKey;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    public AmazonS3 client;

    @PostConstruct
    private void postConstruct() {
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );
        client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, ""))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        if (!client.doesBucketExistV2(bucketName)) {
            client.createBucket(bucketName);
        }
    }
}
