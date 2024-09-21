package faang.school.projectservice.service.project.stage.filters;

import faang.school.projectservice.dto.project.stage.StageFilterDto;
import faang.school.projectservice.dto.project.stage.TaskStatusFilterType;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStatusFilterTest {
    private final StageFilter taskStatusFilter = new TaskStatusFilter();
    private StageFilterDto stageFilterDto;
    private Stream<Stage> stages;

    @Test
    @DisplayName("Is filter applicable with filter and type in dto")
    void taskStatusFilter_isFilterApplicableWithFilterAndTypeInDto() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.IN_PROGRESS)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        boolean result = taskStatusFilter.isApplicable(stageFilterDto);

        assertTrue(result);
    }

    @Test
    @DisplayName("Is filter applicable without type in dto")
    void taskStatusFilter_isFilterApplicableWithoutTypeInDto() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.IN_PROGRESS)
                .build();

        boolean result = taskStatusFilter.isApplicable(stageFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Is filter applicable without filter in dto")
    void taskStatusFilter_isFilterApplicableWithoutFilterInDto() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        boolean result = taskStatusFilter.isApplicable(stageFilterDto);

        assertFalse(result);
    }

    @Test
    @DisplayName("Filtering stage with all done tasks")
    void taskStatusFilter_filteringStageWithAllDoneTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.DONE)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        stages = initStagesWithDoneTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(1L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .status(TaskStatus.DONE)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any done tasks")
    void taskStatusFilter_filteringStageWithAnyDoneTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.DONE)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        stages = initStagesWithDoneTasks();
        List<Stage> expected = List.of(Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.DONE)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.DONE)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none done tasks")
    void taskStatusFilter_filteringStageWithNoneDoneTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.DONE)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        stages = initStagesWithDoneTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(3L)
                .tasks(List.of(
                        Task.builder()
                                .id(4L)
                                .status(TaskStatus.TESTING)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all cancelled tasks")
    void taskStatusFilter_filteringStageWithAllCancelledTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.CANCELLED)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        stages = initStagesWithCancelledTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(1L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .status(TaskStatus.CANCELLED)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any cancelled tasks")
    void taskStatusFilter_filteringStageWithAnyCancelledTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.CANCELLED)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        stages = initStagesWithCancelledTasks();
        List<Stage> expected = List.of(Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.CANCELLED)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.CANCELLED)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none cancelled tasks")
    void taskStatusFilter_filteringStageWithNoneCancelledTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.CANCELLED)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        stages = initStagesWithCancelledTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(3L)
                .tasks(List.of(
                        Task.builder()
                                .id(4L)
                                .status(TaskStatus.TESTING)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all TODO tasks")
    void taskStatusFilter_filteringStageWithAllTodoTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TODO)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        stages = initStagesWithTodoTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(1L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .status(TaskStatus.TODO)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any TOOD tasks")
    void taskStatusFilter_filteringStageWithAnyTodoTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TODO)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        stages = initStagesWithTodoTasks();
        List<Stage> expected = List.of(Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.TODO)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.TODO)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none TODO tasks")
    void taskStatusFilter_filteringStageWithNoneTodoTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TODO)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        stages = initStagesWithTodoTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(3L)
                .tasks(List.of(
                        Task.builder()
                                .id(4L)
                                .status(TaskStatus.TESTING)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all in progress tasks")
    void taskStatusFilter_filteringStageWithAllInProgressTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.IN_PROGRESS)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        stages = initStagesWithInProgressTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(1L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .status(TaskStatus.IN_PROGRESS)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any in progress tasks")
    void taskStatusFilter_filteringStageWithAnyInProgressTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.IN_PROGRESS)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        stages = initStagesWithInProgressTasks();
        List<Stage> expected = List.of(Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.IN_PROGRESS)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.IN_PROGRESS)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none in progress tasks")
    void taskStatusFilter_filteringStageWithNoneInProgressTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.IN_PROGRESS)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        stages = initStagesWithInProgressTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(3L)
                .tasks(List.of(
                        Task.builder()
                                .id(4L)
                                .status(TaskStatus.TESTING)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all review tasks")
    void taskStatusFilter_filteringStageWithAllReviewTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.REVIEW)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        stages = initStagesWithReviewTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(1L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .status(TaskStatus.REVIEW)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any review tasks")
    void taskStatusFilter_filteringStageWithAnyReviewTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.REVIEW)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        stages = initStagesWithReviewTasks();
        List<Stage> expected = List.of(Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.REVIEW)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.REVIEW)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none review tasks")
    void taskStatusFilter_filteringStageWithNoneReviewTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.REVIEW)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        stages = initStagesWithReviewTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(3L)
                .tasks(List.of(
                        Task.builder()
                                .id(4L)
                                .status(TaskStatus.TESTING)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with all testing tasks")
    void taskStatusFilter_filteringStageWithAllTestingTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TESTING)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        stages = initStagesWithTestingTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(1L)
                .tasks(List.of(
                        Task.builder()
                                .id(1L)
                                .status(TaskStatus.TESTING)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with any testing tasks")
    void taskStatusFilter_filteringStageWithAnyTestingTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TESTING)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        stages = initStagesWithTestingTasks();
        List<Stage> expected = List.of(Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.TESTING)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.DONE)
                                        .build()))
                        .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering stage with none testing tasks")
    void taskStatusFilter_filteringStageWithNoneTestingTasks() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TESTING)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        stages = initStagesWithTestingTasks();
        List<Stage> expected = List.of(Stage.builder()
                .stageId(3L)
                .tasks(List.of(
                        Task.builder()
                                .id(4L)
                                .status(TaskStatus.DONE)
                                .build()))
                .build());

        List<Stage> result = taskStatusFilter.apply(stages, stageFilterDto).toList();

        assertEquals(1, result.size());
        assertTrue(result.containsAll(expected));
    }

    @Test
    @DisplayName("Filtering empty list of stages with all type filter")
    void taskStatusFilter_filteringEmptyListWithAllTypeFilter() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TESTING)
                .taskStatusFilterType(TaskStatusFilterType.ALL)
                .build();

        List<Stage> result = taskStatusFilter.apply(Stream.empty(), stageFilterDto).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filtering empty list of stages with any type filter")
    void taskStatusFilter_filteringEmptyListWithAnyTypeFilter() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TESTING)
                .taskStatusFilterType(TaskStatusFilterType.ANY)
                .build();

        List<Stage> result = taskStatusFilter.apply(Stream.empty(), stageFilterDto).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filtering empty list of stages with none type filter")
    void taskStatusFilter_filteringEmptyListWithNoneTypeFilter() {
        stageFilterDto = StageFilterDto.builder()
                .taskStatusFilter(TaskStatus.TESTING)
                .taskStatusFilterType(TaskStatusFilterType.NONE)
                .build();

        List<Stage> result = taskStatusFilter.apply(Stream.empty(), stageFilterDto).toList();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Filtering stages with null arguments")
    void taskStatusFilter_filteringWithNullArguments() {
        stageFilterDto = StageFilterDto.builder()
                .build();
        stages = Stream.empty();
        assertThrows(NullPointerException.class, () -> taskStatusFilter.apply(stages, null));
        assertThrows(NullPointerException.class, () -> taskStatusFilter.apply(null, stageFilterDto));
        assertThrows(NullPointerException.class, () -> taskStatusFilter.apply(null, null));
    }

    private Stream<Stage> initStagesWithTodoTasks() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.TODO)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.TODO)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(4L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());
    }

    private Stream<Stage> initStagesWithInProgressTasks() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.IN_PROGRESS)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.IN_PROGRESS)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(4L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());
    }

    private Stream<Stage> initStagesWithReviewTasks() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.REVIEW)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.REVIEW)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(4L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());
    }

    private Stream<Stage> initStagesWithTestingTasks() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.TESTING)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.DONE)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(4L)
                                        .status(TaskStatus.DONE)
                                        .build()))
                        .build());
    }

    private Stream<Stage> initStagesWithDoneTasks() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.DONE)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.DONE)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(4L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());
    }

    private Stream<Stage> initStagesWithCancelledTasks() {
        return Stream.of(
                Stage.builder()
                        .stageId(1L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(1L)
                                        .status(TaskStatus.CANCELLED)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(2L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(2L)
                                        .status(TaskStatus.CANCELLED)
                                        .build(),
                                Task.builder()
                                        .id(3L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build(),
                Stage.builder()
                        .stageId(3L)
                        .tasks(List.of(
                                Task.builder()
                                        .id(4L)
                                        .status(TaskStatus.TESTING)
                                        .build()))
                        .build());
    }
}
