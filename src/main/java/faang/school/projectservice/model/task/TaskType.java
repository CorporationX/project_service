package faang.school.projectservice.model.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TaskType {
    TASK("task"),
    SUBTASK("subtask");

    private final String name;
}
