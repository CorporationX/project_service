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
@DisplayName("Класс для тестирования фильтрации задач по ключевому слову в имени")
public class TaskKeywordFilterTest {

    @InjectMocks
    private TaskKeywordFilter keywordFilter;

    @Test
    @DisplayName("Тестирование положительного сценария фильтрации")
    void filterTest() {
        TaskFilterDto filterDto = new TaskFilterDto(1L,null, 1L, "some");

        Stream<Task> startedStream = Stream.of(Task.builder()
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

        Stream<Task> expectedStream = Stream.of(Task.builder()
                        .name("someName")
                        .project(new Project())
                        .build(),
                Task.builder()
                        .name("anotherSomeName")
                        .project(new Project())
                        .build());

        Stream<Task> filterdStream = keywordFilter.filter(startedStream, filterDto);

        List<Task> expectedTask = expectedStream.toList();
        List<Task> filteredTask = filterdStream.toList();

        assertEquals(expectedTask, filteredTask);
    }

    @Test
    @DisplayName("Проверка передачи параметра фильтрации - null")
    void isApplicableTest() {
        TaskFilterDto filterDto =
                new TaskFilterDto(1L, TaskStatus.IN_PROGRESS, 1L, null);

        assertFalse(keywordFilter.isApplicable(filterDto));
    }
}
