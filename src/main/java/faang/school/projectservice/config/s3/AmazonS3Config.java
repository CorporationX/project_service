package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${services.s3.accessKey}") private String accessKeyId;
    @Value("${services.s3.secretKey}") private String secretKey;
    @Value("${services.s3.endpoint}") private String s3Endpoint;
    @Value("${services.s3.region}") private String region;

    @Bean(name = "s3Client")
    public AmazonS3 s3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Endpoint, region))
                .withPathStyleAccessEnabled(true)
                .build();
    }

}
