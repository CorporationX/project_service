package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.sub_project.SubProjectCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * Класс-отправитель ивента {@link SubProjectCreatedEvent}
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Component
public class SubProjectCreatedEventPublisher extends AbstractEventPublisher<SubProjectCreatedEvent> {

    @Value("${spring.data.redis.channels.subproject-created-topic.name}")
    private String subProjectCreatedTopic;

    public SubProjectCreatedEventPublisher(RetryTemplate retryTemplate,
                                           RedisTemplate<String, Object> redisTemplate) {
        super(retryTemplate, redisTemplate);
    }

    @Override
    protected String getTopic() {
        return subProjectCreatedTopic;
    }
}