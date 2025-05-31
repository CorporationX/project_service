package faang.school.projectservice.event.vacancy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Primary
public class LogOnlyPublisher implements DomainEventPublisher {
    @Override
    public void publishEvent(DomainEvent event) {
        log.info("Domain event published: type={} payload={}",
                event.getClass().getName(), event.getPayload());
    }
}
