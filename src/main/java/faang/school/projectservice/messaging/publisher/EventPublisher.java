package faang.school.projectservice.messaging.publisher;

public interface EventPublisher<T> {

    void publish(T event);
}
