package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskStatusFilterTest {
    TaskStatusFilter taskStatusFilter = new TaskStatusFilter();
    Task notMatchingtask = Task.builder().status(TaskStatus.TESTING).build();
    Task matchingtask = Task.builder().status(TaskStatus.IN_PROGRESS).build();
    TaskFilterDto filterDto = new TaskFilterDto("222", TaskStatus.IN_PROGRESS,1L);

    @Test
    public void test_when_NotApplicable() {
        filterDto.setStatus(null);
        Assertions.assertFalse(taskStatusFilter.isAppicable(filterDto));
    }

    @Test
    public void test_when_Applicable() {
        Assertions.assertTrue(taskStatusFilter.isAppicable(filterDto));
    }

    @Test public void test_when_keyWordMatches() {
        Assertions.assertTrue(taskStatusFilter.filter(matchingtask,filterDto));
    }

    @Test public void test_when_keyWordDoesNotMatch() {
        Assertions.assertFalse(taskStatusFilter.filter(notMatchingtask,filterDto));
    }
}
