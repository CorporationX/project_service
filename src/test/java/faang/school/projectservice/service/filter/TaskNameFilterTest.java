package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.filter.task.TaskNameFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TaskNameFilterTest {
    private TaskNameFilter taskNameFilter;
    private TaskGettingDto taskGettingDto;
    private static Task task1;
    private static Task task2;

    @BeforeEach
    public void setUp() {
        task1 = Task.builder()
                .name("Task 1")
                .status(TaskStatus.TESTING)
                .build();
        task2 = Task.builder()
                .name("Another Task")
                .status(TaskStatus.CANCELLED)
                .build();
        taskNameFilter = new TaskNameFilter();
        taskGettingDto = TaskGettingDto.builder()
                .word("Task 1")
                .status(TaskStatus.TESTING)
                .build();
    }

    @Test
    void testFilter_WithMatchingName() {
        Stream<Task> tasks = Stream.of(task1, task2);

        Stream<Task> filtered = taskNameFilter.filter(tasks, taskGettingDto);

        assertTrue(filtered.anyMatch(task -> task.getName().equals(task1.getName())));
    }

    @Test
    void testFilter_WithNoMatchingName() {
        Stream<Task> tasks = Stream.of(task1, task2);
        task1 = Task.builder()
                .name("What?")
                .build();

        Stream<Task> filtered = taskNameFilter.filter(tasks, taskGettingDto);

        assertFalse(filtered.anyMatch(task -> task.getName().equals(task1.getName())));
    }

    @Test
    void testIsApplicable_WithNullWord() {
        task1 = Task.builder()
                .name("Task 1")
                .build();
        taskGettingDto = TaskGettingDto.builder()
                .word(null)
                .build();

        boolean filtered = taskNameFilter.isApplicable(taskGettingDto);

        assertFalse(filtered);
    }

    @Test
    void testIsApplicable_WithWord() {
        task1 = Task.builder()
                .name("Task 1")
                .build();
        taskGettingDto = TaskGettingDto.builder()
                .word("Task")
                .build();

        boolean filtered = taskNameFilter.isApplicable(taskGettingDto);

        assertTrue(filtered);
    }
}
