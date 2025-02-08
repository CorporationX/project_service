package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.task.TaskGettingDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.service.filter.task.TaskStatusFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TaskStatusFilterTest {
    private TaskStatusFilter taskStatusFilter;
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
        taskStatusFilter = new TaskStatusFilter();
        taskGettingDto = TaskGettingDto.builder()
                .word("Task 1")
                .status(TaskStatus.TESTING)
                .build();
    }

    @Test
    void testFilter_WithMatchingStatus() {
        Stream<Task> tasks = Stream.of(task1, task2);

        Stream<Task> filtered = taskStatusFilter.filter(tasks, taskGettingDto);

        assertTrue(filtered.anyMatch(task -> task.getStatus().equals(task1.getStatus())));
    }

    @Test
    void testFilter_WithNoMatchingStatus() {
        Stream<Task> tasks = Stream.of(task1, task2);
        task1 = Task.builder()
                .name("What?")
                .status(TaskStatus.DONE)
                .build();

        Stream<Task> filtered = taskStatusFilter.filter(tasks, taskGettingDto);

        assertFalse(filtered.anyMatch(task -> task.getStatus().equals(task1.getStatus())));
    }

    @Test
    void testIsApplicable_WithNullStatus() {
        task1 = Task.builder()
                .name("Task 1")
                .build();
        taskGettingDto = TaskGettingDto.builder()
                .status(null)
                .build();

        boolean filtered = taskStatusFilter.isApplicable(taskGettingDto);

        assertFalse(filtered);
    }

    @Test
    void testIsApplicable_WithStatus() {
        task1 = Task.builder()
                .name("Task 1")
                .status(TaskStatus.TESTING)
                .build();
        taskGettingDto = TaskGettingDto.builder()
                .word("Task")
                .status(TaskStatus.TESTING)
                .build();

        boolean filtered = taskStatusFilter.isApplicable(taskGettingDto);

        assertTrue(filtered);
    }
}
