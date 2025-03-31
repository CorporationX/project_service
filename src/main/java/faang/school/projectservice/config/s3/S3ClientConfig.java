package faang.school.projectservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3ClientConfig {

    private final S3Properties s3Properties;

    @Bean
    public AmazonS3 amazonS3() {

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3Properties.getAccessKey(),
                s3Properties.getSecretKey());

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("us-east-1")
                .build();
    }
}
