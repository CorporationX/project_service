package faang.school.projectservice.service.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "services.s3.project.image")
public class S3Properties {

    private int minHeight;
    private int maxHeight;
    private int maxWidth;
    private int bufferSize;
}
