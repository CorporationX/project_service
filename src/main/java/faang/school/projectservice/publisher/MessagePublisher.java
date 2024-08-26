package faang.school.projectservice.publisher;

public interface MessagePublisher<T> {
    void publish(T message);
}
