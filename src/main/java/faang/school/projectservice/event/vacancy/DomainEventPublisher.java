package faang.school.projectservice.event.vacancy;

public interface DomainEventPublisher {
    void publishEvent(DomainEvent event);
}
