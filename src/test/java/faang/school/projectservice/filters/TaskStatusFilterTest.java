package faang.school.projectservice.filters;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TaskStatusFilterType;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TaskStatusFilterTest {
    private final StageFilter taskStatusFilter = new TaskStatusFilter();
    private StageFilterDto stageFilterDto;
    private Stream<Stage> stages;

    @Test
    @DisplayName("Is filter applicable with filter and type in dto")
    void taskStatusFilter_isFilterApplicableWithFilterAndTypeInDto() {
        stageFilterDto = initStageFilterDto(TaskStatus.IN_PROGRESS, TaskStatusFilterType.ALL);
        boolean result = taskStatusFilter.isApplicable(stageFilterDto);
        assertTrue(result);
    }

    @Test
    @DisplayName("Is filter applicable without type in dto")
    void taskStatusFilter_isFilterApplicableWithoutTypeInDto() {
        stageFilterDto = initStageFilterDto(TaskStatus.IN_PROGRESS, null);
        boolean result = taskStatusFilter.isApplicable(stageFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Is filter applicable without filter in dto")
    void taskStatusFilter_isFilterApplicableWithoutFilterInDto() {
        stageFilterDto = initStageFilterDto(null, TaskStatusFilterType.ALL);
        boolean result = taskStatusFilter.isApplicable(stageFilterDto);
        assertFalse(result);
    }

    @Test
    @DisplayName("Filtering stage with all done tasks")
    void taskStatusFilter_filteringStageWithAllDoneTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.DONE, TaskStatusFilterType.ALL);
        stages = initStages(TaskStatus.DONE, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(1L, List.of(initTask(1L, TaskStatus.DONE))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any done tasks")
    void taskStatusFilter_filteringStageWithAnyDoneTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.DONE, TaskStatusFilterType.ANY);

        stages = initStages(TaskStatus.DONE, TaskStatus.TESTING);
        List<Stage> expected = List.of(
                initStage(1L, List.of(initTask(1L, TaskStatus.DONE))),
                initStage(2L, List.of(initTask(2L, TaskStatus.DONE), initTask(3L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none done tasks")
    void taskStatusFilter_filteringStageWithNoneDoneTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.DONE, TaskStatusFilterType.NONE);
        stages = initStages(TaskStatus.DONE, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(3L, List.of(initTask(4L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all cancelled tasks")
    void taskStatusFilter_filteringStageWithAllCancelledTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.CANCELLED, TaskStatusFilterType.ALL);
        stages = initStages(TaskStatus.CANCELLED, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(1L, List.of(initTask(1L, TaskStatus.CANCELLED))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any cancelled tasks")
    void taskStatusFilter_filteringStageWithAnyCancelledTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.CANCELLED, TaskStatusFilterType.ANY);
        stages = initStages(TaskStatus.CANCELLED, TaskStatus.TESTING);
        List<Stage> expected = List.of(
                initStage(1L, List.of(initTask(1L, TaskStatus.CANCELLED))),
                initStage(2L, List.of(initTask(2L, TaskStatus.CANCELLED), initTask(3L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none cancelled tasks")
    void taskStatusFilter_filteringStageWithNoneCancelledTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.CANCELLED, TaskStatusFilterType.NONE);
        stages = initStages(TaskStatus.CANCELLED, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(3L, List.of(initTask(4L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all TODO tasks")
    void taskStatusFilter_filteringStageWithAllTodoTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.TODO, TaskStatusFilterType.ALL);
        stages = initStages(TaskStatus.TODO, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(1L, List.of(initTask(1L, TaskStatus.TODO))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any TODO tasks")
    void taskStatusFilter_filteringStageWithAnyTodoTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.TODO, TaskStatusFilterType.ANY);
        stages = initStages(TaskStatus.TODO, TaskStatus.TESTING);
        List<Stage> expected = List.of(
                initStage(1L, List.of(initTask(1L, TaskStatus.TODO))),
                initStage(2L, List.of(initTask(2L, TaskStatus.TODO),
                        initTask(3L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none TODO tasks")
    void taskStatusFilter_filteringStageWithNoneTodoTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.TODO, TaskStatusFilterType.NONE);
        stages = initStages(TaskStatus.TODO, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(3L, List.of(initTask(4L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all in progress tasks")
    void taskStatusFilter_filteringStageWithAllInProgressTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.IN_PROGRESS, TaskStatusFilterType.ALL);
        stages = initStages(TaskStatus.IN_PROGRESS, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(1L, List.of(initTask(1L, TaskStatus.IN_PROGRESS))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any in progress tasks")
    void taskStatusFilter_filteringStageWithAnyInProgressTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.IN_PROGRESS, TaskStatusFilterType.ANY);
        stages = initStages(TaskStatus.IN_PROGRESS, TaskStatus.TESTING);
        List<Stage> expected = List.of(
                initStage(1L, List.of(initTask(1L, TaskStatus.IN_PROGRESS))),
                initStage(2L, List.of(initTask(2L, TaskStatus.IN_PROGRESS), initTask(3L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none in progress tasks")
    void taskStatusFilter_filteringStageWithNoneInProgressTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.IN_PROGRESS, TaskStatusFilterType.NONE);
        stages = initStages(TaskStatus.IN_PROGRESS, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(3L, List.of(initTask(4L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all review tasks")
    void taskStatusFilter_filteringStageWithAllReviewTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.REVIEW, TaskStatusFilterType.ALL);
        stages = initStages(TaskStatus.REVIEW, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(1L, List.of(initTask(1L, TaskStatus.REVIEW))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any review tasks")
    void taskStatusFilter_filteringStageWithAnyReviewTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.REVIEW, TaskStatusFilterType.ANY);
        stages = initStages(TaskStatus.REVIEW, TaskStatus.TESTING);
        List<Stage> expected = List.of(
                initStage(1L, List.of(initTask(1L, TaskStatus.REVIEW))),
                initStage(2L, List.of(initTask(2L, TaskStatus.REVIEW), initTask(3L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none review tasks")
    void taskStatusFilter_filteringStageWithNoneReviewTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.REVIEW, TaskStatusFilterType.NONE);
        stages = initStages(TaskStatus.REVIEW, TaskStatus.TESTING);
        List<Stage> expected = List.of(initStage(3L, List.of(initTask(4L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all testing tasks")
    void taskStatusFilter_filteringStageWithAllTestingTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.TESTING, TaskStatusFilterType.ALL);
        stages = initStages(TaskStatus.TESTING, TaskStatus.DONE);
        List<Stage> expected = List.of(initStage(1L, List.of(initTask(1L, TaskStatus.TESTING))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any testing tasks")
    void taskStatusFilter_filteringStageWithAnyTestingTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.TESTING, TaskStatusFilterType.ANY);
        stages = initStages(TaskStatus.TESTING, TaskStatus.DONE);
        List<Stage> expected = List.of(
                initStage(1L, List.of(initTask(1L, TaskStatus.TESTING))),
                initStage(2L, List.of(initTask(2L, TaskStatus.TESTING), initTask(3L, TaskStatus.DONE))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none testing tasks")
    void taskStatusFilter_filteringStageWithNoneTestingTasks() {
        stageFilterDto = initStageFilterDto(TaskStatus.TESTING, TaskStatusFilterType.NONE);
        stages = initStages(TaskStatus.TESTING, TaskStatus.DONE);
        List<Stage> expected = List.of(initStage(3L, List.of(initTask(4L, TaskStatus.DONE))));
        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();
        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering empty list of stages with all type filter")
    void taskStatusFilter_filteringEmptyListWithAllTypeFilter() {
        stageFilterDto = initStageFilterDto(TaskStatus.TESTING, TaskStatusFilterType.ALL);
        List<Stage> result = taskStatusFilter.apply(Stream.empty(), stageFilterDto).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filtering empty list of stages with any type filter")
    void taskStatusFilter_filteringEmptyListWithAnyTypeFilter() {
        stageFilterDto = initStageFilterDto(TaskStatus.TESTING, TaskStatusFilterType.ANY);
        List<Stage> result = taskStatusFilter.apply(Stream.empty(), stageFilterDto).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filtering empty list of stages with none type filter")
    void taskStatusFilter_filteringEmptyListWithNoneTypeFilter() {
        stageFilterDto = initStageFilterDto(TaskStatus.TESTING, TaskStatusFilterType.NONE);
        List<Stage> result = taskStatusFilter.apply(Stream.empty(), stageFilterDto).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filtering stages with null arguments")
    void taskStatusFilter_filteringWithNullArguments() {
        stageFilterDto = initStageFilterDto(null, null);
        stages = Stream.empty();
        assertThrows(NullPointerException.class, () -> taskStatusFilter.apply(stages, null));
        assertThrows(NullPointerException.class, () -> taskStatusFilter.apply(null, stageFilterDto));
        assertThrows(NullPointerException.class, () -> taskStatusFilter.apply(null, null));
    }

    private StageFilterDto initStageFilterDto(TaskStatus taskStatus, TaskStatusFilterType filterType) {
        return StageFilterDto.builder()
                .taskStatusFilter(taskStatus)
                .taskStatusFilterType(filterType)
                .build();
    }

    private Stage initStage(Long id, List<Task> tasks) {
        return Stage.builder()
                .stageId(id)
                .tasks(tasks)
                .build();
    }

    private Task initTask(Long id, TaskStatus status) {
        return Task.builder()
                .id(id)
                .status(status)
                .build();
    }

    private Stream<Stage> initStages(TaskStatus neededTask, TaskStatus notNeededTask) {
        return Stream.of(
                initStage(1L, List.of(initTask(1L, neededTask))),
                initStage(2L, List.of(
                        initTask(2L, neededTask),
                        initTask(3L, notNeededTask))),
                initStage(3L, List.of(initTask(4L, notNeededTask)))
        );
    }
}