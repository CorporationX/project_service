package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 generateAmazonS3client() {
        final AWSCredentials awsCredentials = new BasicAWSCredentials(
                s3Properties.getAccessKey(), s3Properties.getSecretKey()
        );
        final AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                s3Properties.getEndpoint(), null
                        )
                )
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withPathStyleAccessEnabled(true)
                .build();

        String bucketName = s3Properties.getBucketName();
        if (!client.doesBucketExistV2(bucketName)) {
            client.createBucket(bucketName);
        }
        return client;
    }


}
