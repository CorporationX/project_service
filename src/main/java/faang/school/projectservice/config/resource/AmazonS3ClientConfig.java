package faang.school.projectservice.config.resource;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmazonS3ClientConfig {

    private final AmazonS3Properties amazonS3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials awsCredentials =
                new BasicAWSCredentials(amazonS3Properties.getAccessKey(), amazonS3Properties.getSecretKey());
        return AmazonS3Client.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                amazonS3Properties.getEndpoint(),
                                amazonS3Properties.getRegion())
                ).build();
    }
}
