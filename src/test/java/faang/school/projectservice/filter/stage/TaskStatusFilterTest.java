package faang.school.projectservice.filter.stage;

import faang.school.projectservice.model.dto.stage.StageFilterDto;
import faang.school.projectservice.model.entity.Task;
import faang.school.projectservice.model.entity.TaskStatus;
import faang.school.projectservice.model.entity.stage.Stage;
import faang.school.projectservice.model.filter.stage.TaskStatusFilter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TaskStatusFilterTest {

    TaskStatusFilter taskStatusFilter = new TaskStatusFilter();

    @Test
    void testIsNotApplicable() {
        StageFilterDto filterDto = StageFilterDto.builder().build();

        assertFalse(taskStatusFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicable() {
        StageFilterDto filterDto = StageFilterDto.builder().taskStatus(TaskStatus.REVIEW).build();

        assertTrue(taskStatusFilter.isApplicable(filterDto));
    }

    @Test
    void applyOk() {
        Stage stage = Stage
                .builder()
                .tasks(List.of(Task.builder().status(TaskStatus.REVIEW).build()))
                .build();

        StageFilterDto filter = StageFilterDto.builder().taskStatus(TaskStatus.REVIEW).build();

        assertEquals(1, taskStatusFilter.apply(Stream.of(stage), filter).count());
    }
}
