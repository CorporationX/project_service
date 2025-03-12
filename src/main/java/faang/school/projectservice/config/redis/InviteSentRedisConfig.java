package faang.school.projectservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class InviteSentRedisConfig {
    @Value("${spring.data.redis.channels.invitation_channel.name}")
    private String invitationChannel;

    @Bean(name = "invitationChannel")
    public ChannelTopic channelTopic() {return new ChannelTopic(invitationChannel);}

}
