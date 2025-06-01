package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskKeyWordFilterTest {
    TaskKeyWordFilter taskKeyWordFilter = new TaskKeyWordFilter();
    Task notMatchingtask = Task.builder().description("111").build();
    Task matchingtask = Task.builder().description("222").build();
    TaskFilterDto filterDto = new TaskFilterDto("222", TaskStatus.IN_PROGRESS,10L);

    @Test
    public void test_when_NotApplicable() {
        filterDto.setKeyword(null);
        Assertions.assertFalse(taskKeyWordFilter.isAppicable(filterDto));
    }

    @Test
    public void test_when_Applicable() {
        Assertions.assertTrue(taskKeyWordFilter.isAppicable(filterDto));
    }

    @Test public void test_when_keyWordMatches() {
        Assertions.assertTrue(taskKeyWordFilter.filter(matchingtask,filterDto));
    }

    @Test public void test_when_keyWordDoesNotMatch() {
        Assertions.assertFalse(taskKeyWordFilter.filter(notMatchingtask,filterDto));
    }

}
