package faang.school.projectservice.stratagy.stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageDeletionStrategyFactoryTest {

    private StageDeletionStrategyFactory stageDeletionStrategyFactory;

    @Mock
    private MoveTasksToAnotherStageStrategy moveTasksStrategy;

    @Mock
    private CloseTasksStrategy closeTasksStrategy;

    @Mock
    private CascadeDeleteTasksStrategy cascadeDeleteTasksStrategy;

    @BeforeEach
    void setUp() {
        when(moveTasksStrategy.getName()).thenReturn(StageDeletionType.MOVE_TASKS);
        when(closeTasksStrategy.getName()).thenReturn(StageDeletionType.CLOSE_TASKS);
        when(cascadeDeleteTasksStrategy.getName()).thenReturn(StageDeletionType.CASCADE_DELETE);

        stageDeletionStrategyFactory = new StageDeletionStrategyFactory(
                List.of(moveTasksStrategy, closeTasksStrategy, cascadeDeleteTasksStrategy)
        );
    }

    @Test
    void getStrategy_shouldReturnMoveStrategy() {
        assertEquals(moveTasksStrategy, stageDeletionStrategyFactory.getStrategy(StageDeletionType.MOVE_TASKS));
    }

    @Test
    void getStrategy_shouldReturnCloseStrategy() {
        assertEquals(closeTasksStrategy, stageDeletionStrategyFactory.getStrategy(StageDeletionType.CLOSE_TASKS));
    }

    @Test
    void getStrategy_shouldReturnDeleteStrategy() {
        assertEquals(cascadeDeleteTasksStrategy,
                stageDeletionStrategyFactory.getStrategy(StageDeletionType.CASCADE_DELETE));
    }

    @Test
    void getStrategy_shouldReturnMoveTasksStrategyForUnknownType() {
        assertEquals(moveTasksStrategy, stageDeletionStrategyFactory.getStrategy(null));
    }
}