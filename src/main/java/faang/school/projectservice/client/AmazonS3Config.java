package faang.school.projectservice.client;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import faang.school.projectservice.properties.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AmazonS3Config {
    private final AmazonS3Properties amazonS3Properties;

    @Bean()
    public AmazonS3 amazonS3() {
        AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(amazonS3Properties.getAccessKey(), amazonS3Properties.getSecretKey())))
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(amazonS3Properties.getEndpoint(), amazonS3Properties.getRegion()))
                .withPathStyleAccessEnabled(true).build();

        if (!amazonS3Client.doesBucketExistV2(amazonS3Properties.getBucketName())) {
            amazonS3Client.createBucket(new CreateBucketRequest(amazonS3Properties.getBucketName()));
        }

        log.info("The amazon s3 сlient configuration has been initialized");
        return amazonS3Client;
    }
}
