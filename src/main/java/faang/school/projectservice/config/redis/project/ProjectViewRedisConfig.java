package faang.school.projectservice.config.redis.project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class ProjectViewRedisConfig {

    @Value("${spring.data.redis.channels.project_view_channel.name}")
    private String projectViewChannelName;

    @Bean
    public ChannelTopic projectViewTopic() {
        return new ChannelTopic(projectViewChannelName);
    }
}
