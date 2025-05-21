package faang.school.projectservice.event;

public interface DomainEventPublisher {
    void publishEvent(DomainEvent event);
}
