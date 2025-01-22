package faang.school.projectservice.message.producer;

public interface MessagePublisher {
    void publish(String channel,Object message);
}
