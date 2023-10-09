package faang.school.projectservice.publisher;

public interface MessagePublisher {
    void publish(String message);
    void publish(Object event);
}
