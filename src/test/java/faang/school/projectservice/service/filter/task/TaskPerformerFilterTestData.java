package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;

import java.util.stream.Stream;

public class TaskPerformerFilterTestData {
    public static Stream<Task> getStartedStream() {
        return Stream.of(Task.builder()
                        .performerUserId(1L)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .performerUserId(1L)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .performerUserId(5L)
                        .project(new Project())
                        .build());
    }

    public static Stream<Task> getExpectedStream() {
        return Stream.of(Task.builder()
                        .performerUserId(1L)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .performerUserId(1L)
                        .project(new Project())
                        .build());
    }

    public static TaskFilterDto getPerformerFilterDto() {
        return new TaskFilterDto(1L, null, 1L, null);
    }

    public static TaskFilterDto getNullPerformerFilterDto() {
        return new TaskFilterDto(1L, TaskStatus.IN_PROGRESS, null, "someName");
    }
}
