package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.team.TeamEvent;

public interface MessagePublisher<T> {

    void publish(T event);
}
