package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskStatusFilterTest {
    private TaskStatusFilter taskStatusFilter;
    private Task notMatchingtask;
    private Task matchingtask;
    private TaskFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        taskStatusFilter = new TaskStatusFilter();
        notMatchingtask = Task.builder().status(TaskStatus.TESTING).build();
        matchingtask = Task.builder().status(TaskStatus.IN_PROGRESS).build();
        filterDto = new TaskFilterDto("222", TaskStatus.IN_PROGRESS, 1L);
    }

    @Test
    public void test_when_NotApplicable() {
        filterDto.setStatus(null);
        Assertions.assertFalse(taskStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void test_when_Applicable() {
        Assertions.assertTrue(taskStatusFilter.isApplicable(filterDto));
    }

    @Test
    public void test_when_keyWordMatches() {
        Assertions.assertTrue(taskStatusFilter.filter(matchingtask, filterDto));
    }

    @Test
    public void test_when_keyWordDoesNotMatch() {
        Assertions.assertFalse(taskStatusFilter.filter(notMatchingtask, filterDto));
    }
}
