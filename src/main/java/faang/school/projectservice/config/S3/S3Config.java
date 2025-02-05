package faang.school.projectservice.config.S3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;


    public AmazonS3 amazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withPathStyleAccessEnabled(true)
                .build();
    }

}
