package faang.school.projectservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class ProjectRedisConfig {
    @Value("${spring.data.redis.channels.project_channel.name}")
    private String projectChannel;

    @Bean(name = "projectChannel")
    public ChannelTopic projectChannel() {
        return new ChannelTopic(projectChannel);
    }
}
