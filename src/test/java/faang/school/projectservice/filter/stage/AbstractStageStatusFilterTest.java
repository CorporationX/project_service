package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AbstractStageStatusFilterTest {

    private StageAllTasksInStatusFilter stageAllTasksInStatusFilter;
    private StageAnyTaskInStatusFilter stageAnyTaskInStatusFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    void setUp() {
        stageAllTasksInStatusFilter = new StageAllTasksInStatusFilter();
        stageAnyTaskInStatusFilter = new StageAnyTaskInStatusFilter();
        stageFilterDto = Mockito.mock(StageFilterDto.class);
    }

    @Test
    void isApplicableTest_ShouldReturnTrueWhenAllTasksInStatusIsNotNull() {
        TaskStatus taskStatus = TaskStatus.TODO;
        when(stageFilterDto.getAllTasksInStatus()).thenReturn(taskStatus);

        boolean result = stageAllTasksInStatusFilter.isApplicable(stageFilterDto);

        assertTrue(result);
    }

    @Test
    void isApplicableTest_ShouldReturnFalseWhenAllTasksInStatusIsNull() {
        when(stageFilterDto.getAllTasksInStatus()).thenReturn(null);

        boolean result = stageAllTasksInStatusFilter.isApplicable(stageFilterDto);

        assertFalse(result);
    }

    @Test
    void applyTest_ShouldReturnStagesWithAllMatchingTaskStatus() {
        TaskStatus taskStatus = TaskStatus.TODO;
        Stage stage1 = createStageWithTaskStatus(taskStatus, taskStatus, taskStatus);
        Stage stage2 = createStageWithTaskStatus(TaskStatus.IN_PROGRESS, TaskStatus.TODO);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getAllTasksInStatus()).thenReturn(taskStatus);

        Stream<Stage> filteredStages = stageAllTasksInStatusFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertEquals(1, result.size());
        assertEquals(stage1, result.get(0));
    }

    @Test
    void applyTest_ShouldReturnNoStagesIfNotAllTasksMatchStatus() {
        TaskStatus taskStatus = TaskStatus.TODO;
        Stage stage1 = createStageWithTaskStatus(TaskStatus.TODO, TaskStatus.IN_PROGRESS);
        Stage stage2 = createStageWithTaskStatus(TaskStatus.CANCELLED, TaskStatus.TODO);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getAllTasksInStatus()).thenReturn(taskStatus);

        Stream<Stage> filteredStages = stageAllTasksInStatusFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void applyTest_ShouldReturnStagesWithAnyMatchingTaskStatus() {
        TaskStatus taskStatus = TaskStatus.TODO;
        Stage stage1 = createStageWithTaskStatus(taskStatus, TaskStatus.IN_PROGRESS);
        Stage stage2 = createStageWithTaskStatus(TaskStatus.CANCELLED, TaskStatus.TODO);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getAnyTaskInStatus()).thenReturn(taskStatus);

        Stream<Stage> filteredStages = stageAnyTaskInStatusFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertEquals(2, result.size());
    }

    @Test
    void applyTest_ShouldReturnNoStagesIfNoTasksMatchAnyStatus() {
        TaskStatus taskStatus = TaskStatus.TODO;
        Stage stage1 = createStageWithTaskStatus(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED);
        Stage stage2 = createStageWithTaskStatus(TaskStatus.CANCELLED, TaskStatus.IN_PROGRESS);
        List<Stage> stages = Arrays.asList(stage1, stage2);

        when(stageFilterDto.getAnyTaskInStatus()).thenReturn(taskStatus);

        Stream<Stage> filteredStages = stageAnyTaskInStatusFilter.apply(stages.stream(), stageFilterDto);
        List<Stage> result = filteredStages.toList();

        assertTrue(result.isEmpty());
    }

    private Stage createStageWithTaskStatus(TaskStatus... taskStatuses) {
        Stage stage = Mockito.mock(Stage.class);
        List<Task> tasks = Arrays.stream(taskStatuses)
                .map(status -> {
                    Task task = Mockito.mock(Task.class);
                    when(task.getStatus()).thenReturn(status);
                    return task;
                })
                .collect(Collectors.toList());
        when(stage.getTasks()).thenReturn(tasks);
        return stage;
    }
}