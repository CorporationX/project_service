package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services.s3")
@Getter
@Setter
public class S3Config {

    private String accessKey;
    private String secretKey;
    private String endpoint;

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
