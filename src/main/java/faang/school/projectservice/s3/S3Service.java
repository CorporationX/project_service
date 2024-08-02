package faang.school.projectservice.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Service {
    @Value("${services.s3.endpoint}")
    private String endpoint;
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Value("${services.s3.secretKey}")
    private String secretKey;
    private final AWSCredentials credentials;
    public final AmazonS3 client;

    public S3Service() {
        credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );
        client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, ""))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }


}
