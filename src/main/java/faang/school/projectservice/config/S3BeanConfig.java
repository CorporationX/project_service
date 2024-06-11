package faang.school.projectservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import faang.school.projectservice.config.properties.S3Properties;

@Configuration
public class S3BeanConfig {
    @Bean
    AWSCredentialsProvider awsCredentialsProvider(S3Properties properties) {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(properties.getAccessKey(),properties.getSecretKey()));
    }
    
    @Bean
    AmazonS3 amazonS3(AWSCredentialsProvider credentialsProvider,S3Properties properties) {
        return AmazonS3ClientBuilder.standard()
            .withPathStyleAccessEnabled(true)
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), Regions.US_EAST_1.getName()))
            .withCredentials(credentialsProvider).build();
    }
}
