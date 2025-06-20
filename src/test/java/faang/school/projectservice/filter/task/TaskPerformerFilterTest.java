package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskPerformerFilterTest {
    private TaskPerformerFilter taskPerformerFilter;
    private Task notMatchingtask;
    private Task matchingtask;
    private TaskFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        taskPerformerFilter = new TaskPerformerFilter();
        notMatchingtask = Task.builder().performerUserId(10L).build();
        matchingtask = Task.builder().performerUserId(1L).build();
        filterDto = new TaskFilterDto("222", TaskStatus.IN_PROGRESS, 1L);
    }

    @Test
    public void test_when_NotApplicable() {
        filterDto.setPerformerUserId(null);
        Assertions.assertFalse(taskPerformerFilter.isApplicable(filterDto));
    }

    @Test
    public void test_when_Applicable() {
        Assertions.assertTrue(taskPerformerFilter.isApplicable(filterDto));
    }

    @Test
    public void test_when_keyWordMatches() {
        Assertions.assertTrue(taskPerformerFilter.filter(matchingtask, filterDto));
    }

    @Test
    public void test_when_keyWordDoesNotMatch() {
        Assertions.assertFalse(taskPerformerFilter.filter(notMatchingtask, filterDto));
    }
}
