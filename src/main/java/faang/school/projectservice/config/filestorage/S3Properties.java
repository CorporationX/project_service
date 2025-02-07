package faang.school.projectservice.config.filestorage;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "services")
public class S3Properties {
    private final Map<String, String> s3 = new HashMap<>();
}
