package faang.school.projectservice.config.context;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    @Value("${services.s3.secretKey}")
    private String secretKey;
    @Value("${services.s3.accessKey}")
    private String accessKey;
    @Bean
    public AmazonS3 amazonS3() {
        String awsAccessKey = accessKey;
        String awsSecretKey = secretKey;
        Regions clientRegion = Regions.DEFAULT_REGION;

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        return AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}

