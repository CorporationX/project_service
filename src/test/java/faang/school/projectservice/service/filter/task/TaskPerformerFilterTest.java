package faang.school.projectservice.service.filter.task;

import faang.school.projectservice.dto.client.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.service.filter.task.TaskPerformerFilterTestData.getExpectedStream;
import static faang.school.projectservice.service.filter.task.TaskPerformerFilterTestData.getNullPerformerFilterDto;
import static faang.school.projectservice.service.filter.task.TaskPerformerFilterTestData.getPerformerFilterDto;
import static faang.school.projectservice.service.filter.task.TaskPerformerFilterTestData.getStartedStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
@DisplayName("Класс для тестирования фильтрации задач по исполнителю")
public class TaskPerformerFilterTest {
    @InjectMocks
    private TaskPerformerFilter performerFilter;

    @Test
    @DisplayName("Тестирование положительного сценария фильтрации")
    void filterTest() {
        TaskFilterDto filterDto = getPerformerFilterDto();

        Stream<Task> startedStream = getStartedStream();
        Stream<Task> expectedStream = getExpectedStream();

        Stream<Task> filterdStream = performerFilter.filter(startedStream, filterDto);

        List<Task> expectedTask = expectedStream.toList();
        List<Task> filteredTask = filterdStream.toList();

        assertEquals(expectedTask, filteredTask);
    }

    @Test
    @DisplayName("Проверка передачи параметра фильтрации - null")
    void isApplicableTest() {
        TaskFilterDto filterDto = getNullPerformerFilterDto();

        assertFalse(performerFilter.isApplicable(filterDto));
    }
}
