package faang.school.projectservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.dto.event.ProjectViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties properties;
    private final ObjectMapper objectMapper;

    @Bean
    public ChannelTopic channelTopic() {
        String channel = properties.getChannels().get("analitic-project-profile-attendance-channel");
        return new ChannelTopic(channel);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, ProjectViewEvent> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ProjectViewEvent> template = new RedisTemplate<>();
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setConnectionFactory(factory);
        return template;
    }
}
