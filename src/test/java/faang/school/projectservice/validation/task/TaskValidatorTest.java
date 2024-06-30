package faang.school.projectservice.validation.task;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.validation.task.impl.TaskValidatorImpl;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskValidatorTest {

    private static final long taskId1 = 1;
    private static final long taskId2 = 2;
    private static final long taskId3 = 3;
    private static final long taskId4 = 4;
    private static final long taskId5 = 5;
    private static final long taskId6 = 6;
    private static final long taskId7 = 7;
    private static final Task taskWithId1 = Task.builder().id(taskId1).build();
    private static final Task taskWithId2 = Task.builder().id(taskId2).build();
    private static final Task taskWithId3 = Task.builder().id(taskId3).build();
    private static final Task taskWithId4 = Task.builder().id(taskId4).build();
    private static final Task taskWithId5 = Task.builder().id(taskId5).build();
    private static final Task taskWithId6 = Task.builder().id(taskId6).build();
    private static final Task taskWithId7 = Task.builder().id(taskId7).build();
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskValidatorImpl taskValidator;
    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private static Stream<Arguments> provideArgumentsForTestValidateTaskExistence() {
        return Stream.of(
                Arguments.of(taskId1, taskWithId1),
                Arguments.of(taskId2, taskWithId2),
                Arguments.of(taskId3, taskWithId3),
                Arguments.of(taskId4, taskWithId4),
                Arguments.of(taskId5, taskWithId5),
                Arguments.of(taskId6, taskWithId6),
                Arguments.of(taskId7, taskWithId7)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestValidateTaskExistenceShouldThrowException() {
        return Stream.of(
                Arguments.of(taskId1, null),
                Arguments.of(taskId2, null),
                Arguments.of(taskId3, null),
                Arguments.of(taskId4, null),
                Arguments.of(taskId5, null),
                Arguments.of(taskId6, null),
                Arguments.of(taskId7, null)
        );
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateTaskExistence")
    public void testValidateTaskExistence(long taskId, Task task) {
        when(taskRepository.existsById(taskId))
                .thenReturn(true);

        taskValidator.validateTaskExistence(taskId);
        verify(taskRepository, times(1))
                .existsById(idCaptor.capture());
        var actualTaskId = idCaptor.getValue();

        assertEquals(taskId, actualTaskId);

        verifyNoMoreInteractions(taskRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateTaskExistenceShouldThrowException")
    public void testValidateTaskExistenceShouldThrowException(long taskId, Task task) {
        when(taskRepository.existsById(taskId))
                .thenReturn(false);
        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> taskValidator.validateTaskExistence(taskId));
        verify(taskRepository, times(1))
                .existsById(idCaptor.capture());

        var expectedMessage = String.format("a task with %d does not exist", taskId);
        var actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(taskRepository);
    }
}
