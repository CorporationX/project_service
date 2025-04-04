package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 amazonS3(S3Properties s3Properties) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                s3Properties.getAccessKey(), s3Properties.getSecretKey());

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(s3Properties.getEndpoint(), "us-east-1")
                )
                .withPathStyleAccessEnabled(true)
                .build();
    }

    @Bean
    public String initializeBucket(AmazonS3 amazonS3, S3Properties s3Properties) {
        if (!amazonS3.doesBucketExistV2(s3Properties.getBucketName())) {
            amazonS3.createBucket(s3Properties.getBucketName());
        }
        return s3Properties.getBucketName();
    }
}