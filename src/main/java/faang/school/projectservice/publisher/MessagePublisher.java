package faang.school.projectservice.publisher;


import faang.school.projectservice.model.event.ManagerAchievementEvent;

public interface MessagePublisher<T> {
    void publish(T event);
}
