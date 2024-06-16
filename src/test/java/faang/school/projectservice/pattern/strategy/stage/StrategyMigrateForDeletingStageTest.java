package faang.school.projectservice.pattern.strategy.stage;

import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validator.stage.StageValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class StrategyMigrateForDeletingStageTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private StageValidator stageValidator;
    @InjectMocks
    private StrategyMigrateForDeletingStage strategyMigrateForDeletingStage;

    @Captor
    private ArgumentCaptor<Long> stageIdCaptor;
    @Captor
    private ArgumentCaptor<Long> stageToMigrateIdCaptor;

    private static Stream<Arguments> provideArgumentsForTestManageTasksOfStage() {
        return Stream.of(
                Arguments.of(0, Long.MAX_VALUE),
                Arguments.of(1, 150),
                Arguments.of(3, 30),
                Arguments.of(5, 5),
                Arguments.of(30, 3),
                Arguments.of(150, 1),
                Arguments.of(Long.MAX_VALUE, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestManageTasksOfStage")
    public void testManageTasksOfStage(long stageId, long stageToMigrateId) {
        strategyMigrateForDeletingStage.manageTasksOfStage(stageId, stageToMigrateId);

        verify(stageValidator, times(1))
                .validateStageExistence(stageIdCaptor.capture());
        verify(stageValidator, times(1))
                .validateStageForToMigrateExistence(stageToMigrateIdCaptor.capture());
        verify(taskRepository, times(1))
                .updateStageId(stageIdCaptor.capture(), stageToMigrateIdCaptor.capture());

        var actualStageId = stageIdCaptor.getValue();
        var actualStageToMigrateIdCaptor = stageToMigrateIdCaptor.getValue();

        assertEquals(stageId, actualStageId);
        assertEquals(stageToMigrateId, actualStageToMigrateIdCaptor);

        verifyNoMoreInteractions(stageValidator, taskRepository);
    }
}
