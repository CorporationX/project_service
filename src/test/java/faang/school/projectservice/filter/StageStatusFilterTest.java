package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageStatusFilterTest {
    private final StageStatusFilter stageStatusFilter = new StageStatusFilter();
    private List<Stage> stages;

    @BeforeEach
    public void setUp() {
        stages = List.of(
                Stage.builder().tasks(List.of(Task.builder().status(TaskStatus.IN_PROGRESS).build(), Task.builder().status(TaskStatus.DONE).build())).build(),
                Stage.builder().tasks(List.of(Task.builder().status(TaskStatus.IN_PROGRESS).build(), Task.builder().status(TaskStatus.IN_PROGRESS).build())).build()
        );
    }

    @Test
    public void filterByStatus_isAppTrue() {
       StageFilterDto stageFilterDto = StageFilterDto.builder()
                .taskStatus(TaskStatus.DONE).build();
       boolean isApplicable = stageStatusFilter.isApplicable(stageFilterDto);
       assertTrue(isApplicable);
    }

    @Test
    public void filterByStatus_isAppFalse() {
        StageFilterDto stageFilterDto = new StageFilterDto();
        boolean isApplicable = stageStatusFilter.isApplicable(stageFilterDto);
        assertFalse(isApplicable);
    }

    @Test
    public void filterByStatus_correctAnswer() {
        StageFilterDto stageFilterDto = StageFilterDto.builder()
                .taskStatus(TaskStatus.DONE).build();
        Stream<Stage> stageStream = stageStatusFilter.apply(stages.stream(), stageFilterDto);
        assertEquals(1, stageStream.toList().size());
    }
}