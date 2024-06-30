package faang.school.projectservice.pattern.strategy.stage;

import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validation.stage.StageValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class StrategyCascadeForDeletingStageTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageValidator stageValidator;
    @InjectMocks
    private StrategyCascadeForDeletingStage strategyCascadeForDeletingStage;

    @Captor
    private ArgumentCaptor<Long> stageIdCaptor;
    @Captor
    private ArgumentCaptor<Long> stageToMigrateIdCaptor;

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 3, 5, 30, 150, Long.MAX_VALUE})
    public void testManageTasksOfStage(long stageId) {
        strategyCascadeForDeletingStage.manageTasksOfStage(stageId, null);

        verify(stageValidator, times(1))
                .validateExistence(stageIdCaptor.capture());
        verify(taskRepository, times(1))
                .deleteAllByStageId(stageToMigrateIdCaptor.capture());

        var actualStageId = stageIdCaptor.getValue();
        var actualStageToMigrateIdCaptor = stageToMigrateIdCaptor.getValue();

        assertEquals(stageId, actualStageId);
        assertEquals(stageId, actualStageToMigrateIdCaptor);

        verifyNoMoreInteractions(stageValidator, taskRepository);
    }

}
