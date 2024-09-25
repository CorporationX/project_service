package faang.school.projectservice.config.resource;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfig {
    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Bean
    public AmazonS3 amazonS3Client() {


        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,"ru-west-1"))
                .build();


        return s3;
    }
}
