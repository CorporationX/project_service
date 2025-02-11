package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Value("${aws.accessKeyId:}")
    private String accessKeyId;

    @Value("${aws.secretKey:}")
    private String secretKey;

    @Value("${aws.region:}")
    private String region;

    @Bean
    @Primary
    public AmazonS3 amazonS3() {
        if (s3Properties != null && s3Properties.getEndpoint() != null) {
            // Используем настройки из S3Properties
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
        } else if (!accessKeyId.isEmpty() && !secretKey.isEmpty() && !region.isEmpty()) {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretKey);
            return AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(region)
                    .build();
        } else {
            throw new IllegalStateException("Не удалось создать клиент AmazonS3: отсутствуют необходимые настройки.");
        }
    }
}
