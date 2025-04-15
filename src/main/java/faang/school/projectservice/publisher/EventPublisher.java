package faang.school.projectservice.publisher;

public interface EventPublisher<T> {
    void publish(T event);
}
