package faang.school.projectservice.config.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio.project-cover")
public class ProjectCoverMinioProperties {
    private long maxSize;
    private long maxVerticalResolution;
    private long maxHorizontalResolution;
    private double compressedOutputQuality;
    private int compressedOutputScale;
    private String folderName;
}
