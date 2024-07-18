package faang.school.projectservice.validation.teammember;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.team_member.TeamMemberValidatorImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
public class TeamMemberValidatorTest {
    private static final long executorId1 = 1;
    private static final long executorId2 = 2;
    private static final long executorId3 = 3;
    private static final long executorId4 = 4;
    private static final long executorId5 = 5;
    private static final long executorId6 = 6;
    private static final long executorId7 = 7;
    private static final long projectId1 = 1;
    private static final long projectId2 = 2;
    private static final long projectId3 = 3;
    private static final long projectId4 = 4;
    private static final long projectId5 = 5;
    private static final long projectId6 = 6;
    private static final long projectId7 = Long.MAX_VALUE;

    @InjectMocks
    private TeamMemberValidatorImpl teamMemberValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Captor
    private ArgumentCaptor<Long> userIdCaptor;
    @Captor
    private ArgumentCaptor<Long> projectIdCapture;

    private static Stream<Arguments> provideArgumentsForTestValidateExistenceByUserIdAndProjectId() {
        return Stream.of(
                Arguments.of(executorId1, projectId1),
                Arguments.of(executorId2, projectId2),
                Arguments.of(executorId3, projectId3),
                Arguments.of(executorId4, projectId4),
                Arguments.of(executorId5, projectId5),
                Arguments.of(executorId6, projectId6),
                Arguments.of(executorId7, projectId7)
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {executorId1, executorId2, executorId3, executorId4, executorId5, executorId6, Long.MAX_VALUE})
    public void testValidateExistence(long executorId) {
        when(teamMemberRepository.existsById(executorId))
                .thenReturn(true);

        teamMemberValidator.validateExistence(executorId);

        verify(teamMemberRepository, times(1))
                .existsById(userIdCaptor.capture());
        var actualExecutorId = userIdCaptor.getValue();

        verifyNoMoreInteractions(teamMemberRepository);
        assertEquals(executorId, actualExecutorId);
    }

    @ParameterizedTest
    @ValueSource(longs = {executorId1, executorId2, executorId3, executorId4, executorId5, executorId6, Long.MAX_VALUE})
    public void testValidateExistenceShouldThrowException(long executorId) {
        when(teamMemberRepository.existsById(executorId))
                .thenReturn(false);

        NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> teamMemberValidator.validateExistence(executorId));

        verify(teamMemberRepository, times(1))
                .existsById(userIdCaptor.capture());
        var expectedMessage = String.format("TeamMember with id=%d does not exist", executorId);
        var actualMessage = actualException.getMessage();

        verifyNoMoreInteractions(teamMemberRepository);
        assertEquals(expectedMessage, actualMessage);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateExistenceByUserIdAndProjectId")
    public void testValidateExistenceByUserIdAndProjectId(long userId, long projectId) {
        when(teamMemberRepository.existsByUserIdAndProjectId(userId, projectId))
                .thenReturn(true);

        teamMemberValidator.validateExistenceByUserIdAndProjectId(userId, projectId);

        verify(teamMemberRepository, times(1))
                .existsByUserIdAndProjectId(userIdCaptor.capture(), projectIdCapture.capture());
        long actualExecutorId = userIdCaptor.getValue();
        long actualProjectId = projectIdCapture.getValue();

        verifyNoMoreInteractions(teamMemberRepository);
        assertEquals(userId, actualExecutorId);
        assertEquals(projectId, actualProjectId);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateExistenceByUserIdAndProjectId")
    public void testValidateExistenceByUserIdAndProjectIdShouldThrowException(long userId, long projectId) {
        when(teamMemberRepository.existsByUserIdAndProjectId(userId, projectId))
                .thenReturn(false);

        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> teamMemberValidator.validateExistenceByUserIdAndProjectId(userId, projectId));

        String expectedMessage = String.format("TeamMember with %d is not a team member of project with id %d",
                userId, projectId);
        String actualMessage = actualException.getMessage();

        verify(teamMemberRepository, times(1))
                .existsByUserIdAndProjectId(userIdCaptor.capture(), projectIdCapture.capture());
        long actualExecutorId = userIdCaptor.getValue();
        long actualProjectId = projectIdCapture.getValue();

        verifyNoMoreInteractions(teamMemberRepository);
        assertEquals(userId, actualExecutorId);
        assertEquals(projectId, actualProjectId);
        assertEquals(expectedMessage, actualMessage);
    }
}
