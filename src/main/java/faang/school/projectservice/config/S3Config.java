package faang.school.projectservice.config;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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
                .withPathStyleAccessEnabled(true)
                .build();
    }
    @Bean
    RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        return new RestTemplate(requestFactory);
    }
}
