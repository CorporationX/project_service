package faang.school.projectservice.publisher;

import faang.school.projectservice.event.Event;

public interface MessagePublisher<T extends Event> {
    void publish(T event);
}
