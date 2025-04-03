package faang.school.projectservice.config.project;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@ConfigurationProperties(prefix = "project.storage")
@Getter
@Setter
public class ProjectStorageProperties {
    private BigInteger defaultMaxSize;
    private String folderPrefix = "project_";
}
