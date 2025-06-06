package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        log.info("Configuring Amazon S3 client with access key: {}", s3Properties.getAccessKey());
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey());

        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials));
        if (s3Properties.getEndpoint() != null && !s3Properties.getEndpoint().isEmpty()) {
            log.info("Using custom S3 endpoint: {}", s3Properties.getEndpoint());
            AwsClientBuilder.EndpointConfiguration endpointConfig =
                    new AwsClientBuilder.EndpointConfiguration(s3Properties.getEndpoint(), s3Properties.getRegion());
            builder.withEndpointConfiguration(endpointConfig)
                    .withPathStyleAccessEnabled(true);
        } else {
            log.info("Using AWS S3 default endpoint for region: {}", s3Properties.getRegion());
            builder.withRegion(s3Properties.getRegion());
        }
        return builder.build();
    }
}
