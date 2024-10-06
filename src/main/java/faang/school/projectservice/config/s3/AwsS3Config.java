package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan
@RequiredArgsConstructor
@Slf4j
public class AwsS3Config {
    private final AwsS3Params s3Params;

    @Bean
    public AmazonS3 amazonClient() {
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s3Params.getEndpoint(), Regions.EU_CENTRAL_1.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Params.getAccessKey(), s3Params.getSecretKey())))
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
