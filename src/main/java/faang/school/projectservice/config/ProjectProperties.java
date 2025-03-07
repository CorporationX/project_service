package faang.school.projectservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "services.minio")
public class ProjectProperties {
    private String maxFileSize;
    private int targetWidth;
    private int targetHeight;

    public long getMaxFileSizeInBytes() {
        return parseSize(maxFileSize);
    }

    private long parseSize(String size) {
        return org.springframework.util.unit.DataSize.parse(size).toBytes();
    }
}
