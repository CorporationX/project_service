package faang.school.projectservice.messaging;

public interface MessagePublisher<E> {
    void publish(E event);
}
