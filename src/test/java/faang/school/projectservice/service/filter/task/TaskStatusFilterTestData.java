package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;

import java.util.stream.Stream;

public class TaskStatusFilterTestData {
    public static Stream<Task> getStartedStream() {
        return Stream.of(Task.builder()
                        .status(TaskStatus.DONE)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .status(TaskStatus.IN_PROGRESS)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .status(TaskStatus.DONE)
                        .project(new Project())
                        .build());
    }

    public static Stream<Task> getExpectedStream() {
        return Stream.of(Task.builder()
                        .status(TaskStatus.DONE)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .status(TaskStatus.DONE)
                        .project(new Project())
                        .build());
    }

    public static TaskFilterDto getStatusFilterDto() {
        return new TaskFilterDto(1L, TaskStatus.DONE, null, null);
    }

    public static TaskFilterDto getNullStatusFilterDto() {
        return new TaskFilterDto(1L, null, 1L, "someName");
    }
}
