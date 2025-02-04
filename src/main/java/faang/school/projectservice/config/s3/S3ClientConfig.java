package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import faang.school.projectservice.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {
    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        s3Properties.getEndpoint(), Regions.EU_SOUTH_1.name()))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}