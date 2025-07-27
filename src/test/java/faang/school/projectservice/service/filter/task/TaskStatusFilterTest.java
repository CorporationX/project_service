package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
@DisplayName("Класс для тестирования фильтрации задач по статусу")
public class TaskStatusFilterTest {

    @InjectMocks
    private TaskStatusFilter statusFilter;

    @Test
    @DisplayName("Тестирование положительного сценария фильтрации")
    void filterTest() {
        TaskFilterDto filterDto = new TaskFilterDto(TaskStatus.DONE, null, null);

        Stream<Task> startedStream = Stream.of(Task.builder()
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

        Stream<Task> expectedStream = Stream.of(Task.builder()
                        .status(TaskStatus.DONE)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .status(TaskStatus.DONE)
                        .project(new Project())
                        .build());

        Stream<Task> filterdStream = statusFilter.filter(startedStream, filterDto);

        List<Task> expectedTask = expectedStream.toList();
        List<Task> filteredTask = filterdStream.toList();

        assertEquals(expectedTask, filteredTask);
    }

    @Test
    @DisplayName("Проверка передачи параметра фильтрации - null")
    void isApplicableTest() {
        TaskFilterDto filterDto =
                new TaskFilterDto(null, 1L, "someName");

        assertFalse(statusFilter.isApplicable(filterDto));
    }
}
