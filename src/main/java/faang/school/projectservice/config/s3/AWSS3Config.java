package faang.school.projectservice.config.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSS3Config {

    // private String endpoint = "http://127.0.0.1:9001";
    // String endpoint2 = "https://9002-firebase-javabc-kd-project-service-1747252604231.cluster-axf5tvtfjjfekvhwxwkkkzsk2y.cloudworkstations.dev";

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.accessKey}")
    private String username;

    @Value("${services.s3.secretKey}")
    private String password;
    // private ClientConfiguration clientConfig = new ClientConfiguration();

    @Bean
    public AmazonS3 s3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(username, password);
        return AmazonS3ClientBuilder.standard()
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "us-east-1"))
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withPathStyleAccessEnabled(true)
            // .withClientConfiguration(clientConfig)
            .build();
    }
}
