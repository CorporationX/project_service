package faang.school.projectservice.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${services.s3.accessKey}")
    private String awsAccessKeyId;

    @Value("${services.s3.secretKey}")
    private String awsSecretAccessKey;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Bean
    public AmazonS3 s3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "<AWS Region>"))
                .withPathStyleAccessEnabled(true) // Включите, если используете MinIO или аналогичный
                .build();
    }
}
