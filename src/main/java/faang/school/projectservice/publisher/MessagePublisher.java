package faang.school.projectservice.publisher;

import org.springframework.stereotype.Component;

@Component
public interface MessagePublisher {
    public void publish(String message);
}
