package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import faang.school.projectservice.config.properties.S3Properties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Data
@Slf4j
@RequiredArgsConstructor
public class AmazonS3Config {
    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(s3Properties.getEndpoint(), Regions.DEFAULT_REGION.getName()))
                .withPathStyleAccessEnabled(true)
                .build();

        if (!s3client.doesBucketExistV2(s3Properties.getBucketName())) {
            s3client.createBucket(s3Properties.getBucketName());
        }
        log.info("Connected to AmazonS3.");
        return s3client;
    }
}
