package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class StageAnyTaskInStatusFilterTest {

    private StageAnyTaskInStatusFilter stageAnyTaskInStatusFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    void setUp() {
        stageAnyTaskInStatusFilter = new StageAnyTaskInStatusFilter();
        stageFilterDto = Mockito.mock(StageFilterDto.class);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenTaskStatusIsNotNull() {
        TaskStatus taskStatus = TaskStatus.IN_PROGRESS;
        when(stageFilterDto.getAnyTaskInStatus()).thenReturn(taskStatus);

        boolean result = stageAnyTaskInStatusFilter.isApplicable(stageFilterDto);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenTaskStatusIsNull() {
        when(stageFilterDto.getAnyTaskInStatus()).thenReturn(null);

        boolean result = stageAnyTaskInStatusFilter.isApplicable(stageFilterDto);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldReturnStagesWithAnyMatchingTaskStatus() {
        TaskStatus taskStatus = TaskStatus.IN_PROGRESS;
        Stage stage1 = createStageWithTaskStatus(taskStatus);
        Stage stage2 = createStageWithTaskStatus(TaskStatus.DONE);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getAnyTaskInStatus()).thenReturn(taskStatus);

        Stream<Stage> filteredStages = stageAnyTaskInStatusFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertEquals(1, result.size());
        assertEquals(stage1, result.get(0));
    }

    @Test
    void applyTest_ShouldReturnNoStagesIfNoTasksMatchStatus() {
        TaskStatus taskStatus = TaskStatus.IN_PROGRESS;
        Stage stage1 = createStageWithTaskStatus(TaskStatus.DONE);
        Stage stage2 = createStageWithTaskStatus(TaskStatus.CANCELLED);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getAnyTaskInStatus()).thenReturn(taskStatus);

        Stream<Stage> filteredStages = stageAnyTaskInStatusFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertTrue(result.isEmpty());
    }

    private Stage createStageWithTaskStatus(TaskStatus taskStatus) {
        Stage stage = Mockito.mock(Stage.class);
        Task task = Mockito.mock(Task.class);
        when(task.getStatus()).thenReturn(taskStatus);
        when(stage.getTasks()).thenReturn(List.of(task));
        return stage;
    }
}