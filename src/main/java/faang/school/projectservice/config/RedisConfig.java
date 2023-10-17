package faang.school.projectservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.publisher.InviteSentEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.channels.invitation_channel.name}")
    private String invitationTopicName;
    @Value("${spring.data.redis.channels.project_view_channel.name}")
    private String projectViewTopicName;

    @Bean
    public JedisConnectionFactory invitationConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> invitationTemplate(JedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic invitationTopic() {
        return new ChannelTopic(invitationTopicName);
    }

    @Bean
    public ChannelTopic projectViewTopic() {
        return new ChannelTopic(projectViewTopicName);
    }
}
