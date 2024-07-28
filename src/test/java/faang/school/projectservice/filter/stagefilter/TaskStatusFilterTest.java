package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

class TaskStatusFilterTest {
    private Stage stageFirst;
    private Stage stageSecond;
    private TaskStatusFilter taskStatusFilter;
    private StageFilterDto stageFilterDto;

    @BeforeEach
    void setup(){
        stageFirst = new Stage();
        stageSecond = new Stage();
        Task task = new Task();
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        stageFirst.setTasks(tasks);
        stageSecond.setTasks(tasks);
        taskStatusFilter = new TaskStatusFilter();
        stageFilterDto = new StageFilterDto();
        stageFilterDto = mock(StageFilterDto.class);
    }

    @Test
    void testIsApplicableWhenTaskStatusPatternIsNotNull(){
        when(stageFilterDto.getTaskStatusPattern()).thenReturn(TaskStatus.TODO);
        assertTrue(taskStatusFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testIsApplicableWhenTaskStatusPatternIsNull(){
        when(stageFilterDto.getTaskStatusPattern()).thenReturn(null);
        assertFalse(taskStatusFilter.isApplicable(stageFilterDto));
    }

    @Test
    void testApplyWhenStagesMatchTaskStatusPattern(){
        List<Stage> filteredStages = getStageList();
        when(stageFilterDto.getTaskStatusPattern()).thenReturn(TaskStatus.TODO);
        filteredStages = taskStatusFilter.apply(filteredStages.stream(),stageFilterDto).toList();
        assertEquals(2, filteredStages.size());
        assertTrue(filteredStages.contains(filteredStages.get(0)));
        assertTrue(filteredStages.contains(filteredStages.get(1)));
    }

    @Test
    void testApplyWhenNoStagesMatchTaskStatusPattern(){
        List<Stage> filteredStages = getStageList();
        when(stageFilterDto.getTaskStatusPattern()).thenReturn(TaskStatus.CANCELLED);
        filteredStages = taskStatusFilter.apply(filteredStages.stream(),stageFilterDto).toList();
        assertEquals(0, filteredStages.size());
        assertTrue(filteredStages.isEmpty());
    }

    List<Stage> getStageList(){
        stageFirst.getTasks().get(0).setStatus(TaskStatus.TODO);
        stageSecond.getTasks().get(0).setStatus(TaskStatus.TODO);
        return List.of(stageFirst,stageSecond);
    }

}