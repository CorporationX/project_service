package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.jetbrains.annotations.NotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class TaskStatusFilterTest {
    private TaskStatusFilter taskStatusFilter;
    private StageFilterDto stageFilterDto;
    private List<Stage> stages;

    @BeforeEach
    void setup() {
        StageFilterHelper filterHelper = new StageFilterHelper();
        stageFilterDto = filterHelper.stageFilterDto();
        stages = filterHelper.stages();
        taskStatusFilter = new TaskStatusFilter();
    }

    @Test
    void testIsApplicableWhenTaskStatusPatternIsNotNull() {
        assertTrue(taskStatusFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testIsApplicableWhenTaskStatusPatternIsNull() {
        stageFilterDto.setTaskStatusPattern(null);
        assertFalse(taskStatusFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testApplyWhenStagesMatchTaskStatusPattern() {
        List<Stage> filteredStages = getStages();
        assertEquals(2, filteredStages.size());
        assertTrue(filteredStages.containsAll(stages));
    }

    @Test
    void testApplyWhenNoStagesMatchTaskStatusPattern() {
        stageFilterDto.setTaskStatusPattern(TaskStatus.IN_PROGRESS);
        List<Stage> filteredStages = getStages();
        assertEquals(0, filteredStages.size());
        assertTrue(filteredStages.isEmpty());
    }

    private @NotNull List<Stage> getStages() {
        return taskStatusFilter.apply(stages
                        .stream(), stageFilterDto)
                .toList();
    }
}