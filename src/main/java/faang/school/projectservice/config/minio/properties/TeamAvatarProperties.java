package faang.school.projectservice.config.minio.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio.team-avatar")
public class TeamAvatarProperties {
    private long maxFileSize;
    private int maxImageSize;
    private String folderName;
}
