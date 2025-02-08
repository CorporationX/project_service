package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.filter.task.PerformerFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PerformerFilterTest {
    private PerformerFilter performerFilter;
    private TaskGettingDto taskGettingDto;
    private static Task task1;
    private static Task task2;

    @BeforeEach
    public void setUp() {
        task1 = Task.builder()
                .name("Task 1")
                .status(TaskStatus.TESTING)
                .performerUserId(1L)
                .build();
        task2 = Task.builder()
                .name("Another Task")
                .status(TaskStatus.CANCELLED)
                .performerUserId(2L)
                .build();
        performerFilter = new PerformerFilter();
        taskGettingDto = TaskGettingDto.builder()
                .word("Task 1")
                .status(TaskStatus.TESTING)
                .performerUserId(1L)
                .build();
    }

    @Test
    void testFilter_WithMatchingPerformer() {
        Stream<Task> tasks = Stream.of(task1, task2);

        Stream<Task> filtered = performerFilter.filter(tasks, taskGettingDto);

        assertTrue(filtered.anyMatch(task -> task.getPerformerUserId().equals(task1.getPerformerUserId())));
    }

    @Test
    void testFilter_WithNoMatchingPerformer() {
        Stream<Task> tasks = Stream.of(task1, task2);
        task1 = Task.builder()
                .name("What?")
                .performerUserId(12L)
                .build();

        Stream<Task> filtered = performerFilter.filter(tasks, taskGettingDto);

        assertFalse(filtered.anyMatch(task -> task.getPerformerUserId().equals(task1.getPerformerUserId())));
    }

    @Test
    void testIsApplicable_WithNullPerformer() {
        task1 = Task.builder()
                .name("Task 1")
                .build();
        taskGettingDto = TaskGettingDto.builder()
                .status(null)
                .performerUserId(null)
                .build();

        boolean filtered = performerFilter.isApplicable(taskGettingDto);

        assertFalse(filtered);
    }

    @Test
    void testIsApplicable_WithPerformer() {
        task1 = Task.builder()
                .name("Task 1")
                .status(TaskStatus.TESTING)
                .performerUserId(1L)
                .build();
        taskGettingDto = TaskGettingDto.builder()
                .word("Task")
                .status(TaskStatus.TESTING)
                .performerUserId(2L)
                .build();

        boolean filtered = performerFilter.isApplicable(taskGettingDto);

        assertTrue(filtered);
    }
}
