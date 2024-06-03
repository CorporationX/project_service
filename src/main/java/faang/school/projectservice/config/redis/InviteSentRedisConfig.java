package faang.school.projectservice.config.redis;

import faang.school.projectservice.publisher.InviteSentPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class InviteSentRedisConfig {

    @Value("${spring.data.redis.channels.invitation_channel.name}")
    private String topicName;

    @Bean
    ChannelTopic inviteSentTopic() {
        return new ChannelTopic(topicName);
    }

    @Bean
    InviteSentPublisher completedGoalPublisher(RedisTemplate<String, Object> redisTemplate) {
        return new InviteSentPublisher(redisTemplate, inviteSentTopic());
    }
}
