package faang.school.projectservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {
    @Value("${app.config.max_team_avatar_side_length}")
    private int maxTeamAvatarSideLength;

    @Value("${app.config.max_team_avatar_size_in_mb}")
    private int maxTeamAvatarSize;

    @Value("${app.config.minio_bucket_name}")
    private String minioBucketName;

    @Value("${app.config.minio_login}")
    private String minioLogin;

    @Value("${app.config.minio_password}")
    private String minioPassword;

    @Value("${app.config.minio_endpoint}")
    private String minioEndpoint;
}
