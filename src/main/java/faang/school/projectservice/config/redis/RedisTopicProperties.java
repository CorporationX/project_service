package faang.school.projectservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RedisTopicProperties {

    @Value("${spring.data.redis.channels.project-view-channel}")
    private String projectViewChannel;
}
