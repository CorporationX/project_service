package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.util.project.TaskUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Класс для тестирования фильтрации задач по исполнителю")
public class TaskPerformerFilterTest {
    @Mock
    private TaskUtil taskUtil;

    @InjectMocks
    private TaskPerformerFilter performerFilter;

    @Test
    @DisplayName("Тестирование положительного сценария фильтрации")
    void filterTest() {
        TaskFilterDto filterDto = new TaskFilterDto(null, 1L, null);
        when(taskUtil.isInTeam(any())).thenReturn(true);

        Stream<Task> startedStream = Stream.of(Task.builder()
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

        Stream<Task> expectedStream = Stream.of(Task.builder()
                        .performerUserId(1L)
                        .project(new Project())
                        .build(),
                Task.builder()
                        .performerUserId(1L)
                        .project(new Project())
                        .build());

        Stream<Task> filterdStream = performerFilter.filter(startedStream, filterDto);

        List<Task> expectedTask = expectedStream.toList();
        List<Task> filteredTask = filterdStream.toList();

        assertEquals(expectedTask, filteredTask);
    }

    @Test
    @DisplayName("Проверка передачи параметра фильтрации - null")
    void isApplicableTest() {
        TaskFilterDto filterDto =
                new TaskFilterDto(TaskStatus.IN_PROGRESS, null, "someName");

        assertFalse(performerFilter.isApplicable(filterDto));
    }
}
