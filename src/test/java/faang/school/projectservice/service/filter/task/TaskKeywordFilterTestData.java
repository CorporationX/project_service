package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;

import java.util.stream.Stream;

public class TaskKeywordFilterTestData {
    public static Stream<Task> getStartedStream() {
        return Stream.of(Task.builder()
                        .name("someName")
                        .project(new Project())
                        .build(),
                Task.builder()
                        .name("without")
                        .project(new Project())
                        .build(),
                Task.builder()
                        .name("anotherSomeName")
                        .project(new Project())
                        .build());
    }

    public static Stream<Task> getExpectedStream() {
        return Stream.of(Task.builder()
                        .name("someName")
                        .project(new Project())
                        .build(),
                Task.builder()
                        .name("anotherSomeName")
                        .project(new Project())
                        .build());
    }

    public static TaskFilterDto getKeywordFilterDto() {
        return new TaskFilterDto(1L,null, 1L, "some");
    }

    public static TaskFilterDto getNullKeywordFilterDto() {
        return new TaskFilterDto(1L, TaskStatus.IN_PROGRESS, 1L, null);
    }
}
