package faang.school.projectservice.validation.project;


import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.project.impl.ProjectValidatorImpl;
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
public class ProjectValidatorTest {
    private static final long projectId1 = 1;
    private static final long projectId2 = 2;
    private static final long projectId3 = 3;
    private static final long projectId4 = 100_000_000;
    private static final long projectId5 = 200_000_000;
    private static final long projectId6 = 3_000_000_000L;
    private static final long projectId7 = Long.MAX_VALUE;
    private static final long userId1 = 1;
    private static final long userId2 = 2;
    private static final long userId3 = 3;
    private static final long userId4 = 100_000_000;
    private static final long userId5 = 200_000_000;
    private static final long userId6 = 3_000_000_000L;
    private static final long userId7 = Long.MAX_VALUE;

    @InjectMocks
    private ProjectValidatorImpl projectValidator;
    @Mock
    private ProjectRepository projectRepository;
    @Captor
    private ArgumentCaptor<Long> projectIdCaptor;
    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    private static Stream<Arguments> provideArgumentsForTestValidateProjectIdAndCurrentUserId() {
        return Stream.of(
                Arguments.of(projectId1, userId1),
                Arguments.of(projectId2, userId2),
                Arguments.of(projectId3, userId3),
                Arguments.of(projectId4, userId4),
                Arguments.of(projectId5, userId5),
                Arguments.of(projectId6, userId6),
                Arguments.of(projectId7, userId7)
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {projectId1, projectId2, projectId3, projectId4, projectId5, projectId6, projectId7})
    public void testValidateProjectExistence(long projectId) {
        when(projectRepository.existsById(projectId))
                .thenReturn(true);

        projectValidator.validateProjectExistence(projectId);
        verify(projectRepository, times(1))
                .existsById(projectIdCaptor.capture());
        long actualProjectId = projectIdCaptor.getValue();

        assertEquals(projectId, actualProjectId);

        verifyNoMoreInteractions(projectRepository);
    }

    @ParameterizedTest
    @ValueSource(longs = {projectId1, projectId2, projectId3, projectId4, projectId5, projectId6, projectId7})
    public void testValidateProjectExistenceShouldThrowException(long projectId) {
        when(projectRepository.existsById(projectId))
                .thenReturn(false);
        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectExistence(projectId));
        verify(projectRepository, times(1))
                .existsById(projectIdCaptor.capture());

        String expectedMessage = String.format("a project with id %d does not exist", projectId);
        String actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(projectRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateProjectIdAndCurrentUserId")
    public void testValidateProjectIdAndCurrentUserId(long projectId, long currentUserId) {
        when(projectRepository.checkUserIsProjectOwner(projectId, currentUserId))
                .thenReturn(true);

        projectValidator.validateProjectIdAndCurrentUserId(projectId, currentUserId);

        verify(projectRepository, times(1))
                .checkUserIsProjectOwner(projectIdCaptor.capture(), userIdCaptor.capture());
        long actualProjectId = projectIdCaptor.getValue();
        long actualUserId = userIdCaptor.getValue();

        verifyNoMoreInteractions(projectRepository);
        assertEquals(projectId, actualProjectId);
        assertEquals(currentUserId, actualUserId);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateProjectIdAndCurrentUserId")
    public void testValidateProjectIdAndCurrentUserIdShouldThrowException(long projectId, long currentUserId) {
        when(projectRepository.checkUserIsProjectOwner(projectId, currentUserId))
                .thenReturn(false);

        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectIdAndCurrentUserId(projectId, currentUserId));

        String expectedExceptionMessage = String.format("a user with id %d does not owe a project with id %d",
                currentUserId, projectId);
        String actualExceptionMessage = actualException.getMessage();

        verify(projectRepository, times(1))
                .checkUserIsProjectOwner(projectIdCaptor.capture(), userIdCaptor.capture());

        long actualProjectId = projectIdCaptor.getValue();
        long actualUserId = userIdCaptor.getValue();

        verifyNoMoreInteractions(projectRepository);
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
        assertEquals(projectId, actualProjectId);
        assertEquals(currentUserId, actualUserId);
    }
}
