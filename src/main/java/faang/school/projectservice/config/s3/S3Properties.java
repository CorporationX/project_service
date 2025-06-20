package faang.school.projectservice.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "services.s3")
@Component
public class S3Properties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private boolean isMocked;
    private String region;
    private boolean pathStyleAccess;

    private Filename filename = new Filename();
    private ExecutorConfig executorConfig = new ExecutorConfig();

    @Getter
    @Setter
    public static class Filename {
        private String folderTemplate;
        private String keyTemplate;
        private String keyWhitelist;
        private String metadataWhitelist;
        private String replacementChar;
    }

    @Getter
    @Setter
    public static class ExecutorConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private String threadNamePrefix;
    }
}