package faang.school.projectservice.model;

import java.util.List;

public enum TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    TESTING,
    DONE,
    CANCELLED;

    public static List<TaskStatus> getAll() {
        return List.of(TaskStatus.values());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}