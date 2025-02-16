package faang.school.projectservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisTopicFactory {
    @Value("$spring.data.redis.topic.donation")
    private String donationTopic;

    @Value("${spring.data.redis.channels.team_channel.name}")
    private String teamTopic;

    @Bean
    public ChannelTopic donationTopic() {
        return new ChannelTopic(donationTopic);
    }

    @Bean
    public ChannelTopic teamTopic() {
        return new ChannelTopic(teamTopic);
    }
}
