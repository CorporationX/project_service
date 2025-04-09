package faang.school.projectservice.config.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;


@Getter
@Setter
@ConfigurationProperties(prefix = "services.s3")
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class S3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}