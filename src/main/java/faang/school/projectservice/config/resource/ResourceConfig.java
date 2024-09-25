package faang.school.projectservice.config.resource;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceConfig {

    @Bean
    public AmazonS3Client amazonS3Client() {
        AmazonS3Client client = new AmazonS3Client();
        client.setRegion(Region.getRegion(Regions.US_WEST_2));
        return client;
    }
}
