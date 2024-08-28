package faang.school.projectservice.config.amazon;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class AmazonS3Config {
//
//    @Value("${services.s3.accessKey}")
//    private String accessKey;
//    @Value("${services.s3.secretKey}")
//    private String secretKey;
//    @Value("${services.s3.endpoint}")
//    private String endpoint;
//    @Value("${services.s3.region}")
//    private String REGION;
//
//    @Bean
//    public AmazonS3 s3Client() {
//        return AmazonS3ClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, REGION))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials( accessKey, secretKey)))
//                .withPathStyleAccessEnabled(true)
//                .build();
//    }
//}
