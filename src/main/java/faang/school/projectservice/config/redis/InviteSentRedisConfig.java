package faang.school.projectservice.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class InviteSentRedisConfig {

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("invitation_channel");
    }

}
