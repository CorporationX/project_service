package faang.school.projectservice.publisher;

import faang.school.projectservice.dto.project.ProjectViewEvent;

public interface EventPublisher<T> {
    void publish(T t);
}
