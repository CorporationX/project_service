package faang.school.projectservice.validator.teammember;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.validator.teammember.impl.TeamMemberValidatorImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
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
    private static final TeamMember executorWithId1 = TeamMember.builder().id(executorId1).build();
    private static final TeamMember executorWithId2 = TeamMember.builder().id(executorId2).build();
    private static final TeamMember executorWithId3 = TeamMember.builder().id(executorId3).build();
    private static final TeamMember executorWithId4 = TeamMember.builder().id(executorId4).build();
    private static final TeamMember executorWithId5 = TeamMember.builder().id(executorId5).build();
    private static final TeamMember executorWithId6 = TeamMember.builder().id(executorId6).build();
    private static final TeamMember executorWithId7 = TeamMember.builder().id(executorId7).build();
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @InjectMocks
    private TeamMemberValidatorImpl teamMemberValidator;
    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private static Stream<Arguments> provideArgumentsForTestValidateTeamMemberExistenceShouldReturnExecutor() {
        return Stream.of(
                Arguments.of(executorId1, executorWithId1),
                Arguments.of(executorId2, executorWithId2),
                Arguments.of(executorId3, executorWithId3),
                Arguments.of(executorId4, executorWithId4),
                Arguments.of(executorId5, executorWithId5),
                Arguments.of(executorId6, executorWithId6),
                Arguments.of(executorId7, executorWithId7)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestValidateTeamMemberExistenceShouldThrowException() {
        return Stream.of(
                Arguments.of(executorId1, null),
                Arguments.of(executorId2, null),
                Arguments.of(executorId3, null),
                Arguments.of(executorId4, null),
                Arguments.of(executorId5, null),
                Arguments.of(executorId6, null),
                Arguments.of(executorId7, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateTeamMemberExistenceShouldReturnExecutor")
    public void testValidateTeamMemberExistenceShouldReturnExecutor(long executorId, TeamMember executor) {
        when(teamMemberJpaRepository.findById(executorId))
                .thenReturn(Optional.of(executor));

        var actualExecutor = teamMemberValidator.validateTeamMemberExistence(executorId);
        verify(teamMemberJpaRepository, times(1))
                .findById(idCaptor.capture());
        var actualExecutorId = idCaptor.getValue();

        assertEquals(executorId, actualExecutorId);
        assertEquals(executor, actualExecutor);

        verifyNoMoreInteractions(teamMemberJpaRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateTeamMemberExistenceShouldThrowException")
    public void testValidateTeamMemberExistenceShouldThrowException(long executorId, TeamMember executor) {
        when(teamMemberJpaRepository.findById(executorId))
                .thenReturn(Optional.ofNullable(executor));
        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> teamMemberValidator.validateTeamMemberExistence(executorId));
        verify(teamMemberJpaRepository, times(1))
                .findById(idCaptor.capture());

        var expectedMessage = String.format("a team member with %d does not exist", executorId);
        var actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(teamMemberJpaRepository);
    }
}
