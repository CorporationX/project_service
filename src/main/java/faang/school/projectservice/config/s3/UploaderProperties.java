package faang.school.projectservice.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "uploader")
public record UploaderProperties(DataSize maxFileSize) {
}
