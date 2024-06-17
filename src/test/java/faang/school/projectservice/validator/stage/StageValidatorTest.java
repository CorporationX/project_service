package faang.school.projectservice.validator.stage;

import faang.school.projectservice.dto.stage.NewStageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.project.impl.ProjectValidatorImpl;
import faang.school.projectservice.validator.stage.impl.StageValidatorImpl;
import faang.school.projectservice.validator.task.impl.TaskValidatorImpl;
import faang.school.projectservice.validator.teammember.impl.TeamMemberValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageValidatorTest {

    private static final long projectId1 = 12;
    private static final long projectId2 = 22;
    private static final long projectId3 = 32;
    private static final long projectId4 = 42;
    private static final long projectId5 = 52;
    private static final long projectId6 = 62;
    private static final long projectId7 = 72;

    private static final long taskId31 = 31;
    private static final long executorId41 = 41;

    private static final long stageId1 = 1;
    private static final long stageId2 = 2;
    private static final long stageId3 = 3;
    private static final long stageId4 = 4;
    private static final long stageId5 = 5;
    private static final long stageId6 = 6;
    private static final long stageId7 = 7;

    private static final Stage stageWithId1 = Stage.builder().stageId(stageId1).build();
    private static final Stage stageWithId2 = Stage.builder().stageId(stageId2).build();
    private static final Stage stageWithId3 = Stage.builder().stageId(stageId3).build();
    private static final Stage stageWithId4 = Stage.builder().stageId(stageId4).build();
    private static final Stage stageWithId5 = Stage.builder().stageId(stageId5).build();
    private static final Stage stageWithId6 = Stage.builder().stageId(stageId6).build();
    private static final Stage stageWithId7 = Stage.builder().stageId(stageId7).build();

    private static final List<Long> taskIds = List.of(32L, 43L, 98L);

    private static final List<Long> executorIds = List.of(42L, 54L, 65L, 84L);

    private static final NewStageDto newStageDtoWithId1 = NewStageDto.builder()
            .projectId(projectId1)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId2 = NewStageDto.builder()
            .projectId(projectId2)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId3 = NewStageDto.builder()
            .projectId(projectId3)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId4 = NewStageDto.builder()
            .projectId(projectId4)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId5 = NewStageDto.builder()
            .projectId(projectId5)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId6 = NewStageDto.builder()
            .projectId(projectId6)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();
    private static final NewStageDto newStageDtoWithId7 = NewStageDto.builder()
            .projectId(projectId7)
            .tasksIds(taskIds)
            .executorsIds(executorIds)
            .build();

    private static final Task taskWithId32 = Task.builder().id(taskId31).build();
    private static final TeamMember executorWithId42 = TeamMember.builder().id(executorId41).build();


    private static final Project projectWithId1 = Project.builder()
            .id(projectId1)
            .build();
    private static final Stage expectedStageWithId1 = Stage.builder()
            .stageId(stageId1)
            .project(projectWithId1)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();
    private static final Project projectWithId2 = Project.builder()
            .id(projectId2)
            .build();
    private static final Stage expectedStageWithId2 = Stage.builder()
            .stageId(stageId2).project(projectWithId2)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();
    private static final Project projectWithId3 = Project.builder()
            .id(projectId3)
            .build();
    private static final Stage expectedStageWithId3 = Stage.builder()
            .stageId(stageId3).project(projectWithId3)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();
    private static final Project projectWithId4 = Project.builder()
            .id(projectId4)
            .build();
    private static final Stage expectedStageWithId4 = Stage.builder()
            .stageId(stageId4)
            .project(projectWithId4)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();
    private static final Project projectWithId5 = Project.builder()
            .id(projectId5)
            .build();
    private static final Stage expectedStageWithId5 = Stage.builder()
            .stageId(stageId5)
            .project(projectWithId5)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();
    private static final Project projectWithId6 = Project.builder()
            .id(projectId6)
            .build();
    private static final Stage expectedStageWithId6 = Stage.builder()
            .stageId(stageId6).project(projectWithId6)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();
    private static final Project projectWithId7 = Project.builder()
            .id(projectId7)
            .build();
    private static final Stage expectedStageWithId7 = Stage.builder()
            .stageId(stageId7)
            .project(projectWithId7)
            .tasks(List.of(taskWithId32, taskWithId32, taskWithId32))
            .executors(List.of(executorWithId42, executorWithId42, executorWithId42, executorWithId42))
            .build();


    @Mock
    private TeamMemberValidatorImpl teamMemberValidator;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private ProjectValidatorImpl projectValidator;
    @Mock
    private TaskValidatorImpl taskValidator;
    @InjectMocks
    private StageValidatorImpl stageValidator;
    @Captor
    private ArgumentCaptor<Long> idCaptor;
    @Captor
    private ArgumentCaptor<Long> idCaptor1;
    @Captor
    private ArgumentCaptor<Long> idCaptor2;

    private static Stream<Arguments> provideArgumentsForTestValidateStageExistenceShouldReturnStage() {
        return Stream.of(
                Arguments.of(stageId1, stageWithId1),
                Arguments.of(stageId2, stageWithId2),
                Arguments.of(stageId3, stageWithId3),
                Arguments.of(stageId4, stageWithId4),
                Arguments.of(stageId5, stageWithId5),
                Arguments.of(stageId6, stageWithId6),
                Arguments.of(stageId7, stageWithId7)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestValidateStageExistenceShouldThrowException() {
        return Stream.of(
                Arguments.of(stageId1, null),
                Arguments.of(stageId2, null),
                Arguments.of(stageId3, null),
                Arguments.of(stageId4, null),
                Arguments.of(stageId5, null),
                Arguments.of(stageId6, null),
                Arguments.of(stageId7, null)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestValidateCreation() {

        return Stream.of(
                Arguments.of(newStageDtoWithId1),
                Arguments.of(newStageDtoWithId2),
                Arguments.of(newStageDtoWithId3),
                Arguments.of(newStageDtoWithId4),
                Arguments.of(newStageDtoWithId5),
                Arguments.of(newStageDtoWithId6),
                Arguments.of(newStageDtoWithId7)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateStageExistenceShouldReturnStage")
    public void testValidateStageExistenceShouldReturnStage(long stageId, Stage stage) {
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

        stageValidator.validateStageExistence(stageId);
        verify(stageRepository, times(1)).findById(idCaptor.capture());
        var actualStageId = idCaptor.getValue();

        assertEquals(stageId, actualStageId);

        verifyNoMoreInteractions(stageRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateStageExistenceShouldThrowException")
    public void testValidateStageExistenceShouldThrowException(long stageId, Stage stage) {
        when(stageRepository.findById(stageId)).thenReturn(Optional.ofNullable(stage));
        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> stageValidator.validateStageExistence(stageId));
        verify(stageRepository, times(1)).findById(idCaptor.capture());

        var expectedMessage = String.format("a stage with %d does not exist", stageId);
        var actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(stageRepository);
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateCreation")
    public void testValidateCreation(NewStageDto newStageDto) {
        doNothing().when(projectValidator).validateProjectExistence(newStageDto.getProjectId());
        doNothing().when(taskValidator).validateTaskExistence(anyLong());
        doNothing().when(teamMemberValidator).validateTeamMemberExistence(anyLong());

        stageValidator.validateCreation(newStageDto);

        verify(projectValidator, times(1)).validateProjectExistence(idCaptor.capture());
        verify(taskValidator, times(3)).validateTaskExistence(idCaptor1.capture());
        verify(teamMemberValidator, times(4)).validateTeamMemberExistence(idCaptor2.capture());

        var actualProjectId = idCaptor.getValue();
        var actualTaskIds = idCaptor1.getAllValues();
        var actualExecutorsIds = idCaptor2.getAllValues();

        assertEquals(newStageDto.getProjectId(), actualProjectId);
        assertEquals(newStageDto.getTasksIds(), actualTaskIds);
        assertEquals(newStageDto.getExecutorsIds(), actualExecutorsIds);

        verifyNoMoreInteractions(projectValidator, taskValidator, teamMemberValidator);
    }

    @Test
    public void testValidateStageForToMigrateExistenceShouldThrowException() {
        var exception = assertThrows(DataValidationException.class, () -> {
            stageValidator.validateStageForToMigrateExistence(Long.getLong(""));
        });

        var actual = exception.getMessage();
        var expected = "a stage does not exist";

        assertEquals(expected, actual);
    }
}
