package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.model.Task;

import java.util.stream.Stream;

public interface TaskGetting {
    Stream<Task> filter(Stream<Task> stream, TaskGettingDto request);

    boolean isApplicable(TaskGettingDto request);
}
