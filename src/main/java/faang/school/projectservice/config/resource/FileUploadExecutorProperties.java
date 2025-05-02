package faang.school.projectservice.config.resource;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "task-executor.file-upload")
public class FileUploadExecutorProperties {

    @NotNull
    private Integer corePoolSize;

    @NotNull
    private Integer maxPoolSize;

    @NotNull
    private Integer queueCapacity;
}
