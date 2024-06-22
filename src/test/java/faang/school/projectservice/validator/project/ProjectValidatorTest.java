package faang.school.projectservice.validator.project;


import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.project.impl.ProjectValidatorImpl;
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
public class ProjectValidatorTest {

    private static final long projectId1 = 1;
    private static final long projectId2 = 2;
    private static final long projectId3 = 3;
    private static final long projectId4 = 4;
    private static final long projectId5 = 5;
    private static final long projectId6 = 6;
    private static final long projectId7 = 7;
    private static final Project projectWithId1 = Project.builder().id(projectId1).build();
    private static final Project projectWithId2 = Project.builder().id(projectId2).build();
    private static final Project projectWithId3 = Project.builder().id(projectId3).build();
    private static final Project projectWithId4 = Project.builder().id(projectId4).build();
    private static final Project projectWithId5 = Project.builder().id(projectId5).build();
    private static final Project projectWithId6 = Project.builder().id(projectId6).build();
    private static final Project projectWithId7 = Project.builder().id(projectId7).build();
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectValidatorImpl projectValidator;
    @Captor
    private ArgumentCaptor<Long> idCaptor;

    private static Stream<Arguments> provideArgumentsForTestValidateProjectExistence() {
        return Stream.of(
                Arguments.of(projectId1, projectWithId1),
                Arguments.of(projectId2, projectWithId2),
                Arguments.of(projectId3, projectWithId3),
                Arguments.of(projectId4, projectWithId4),
                Arguments.of(projectId5, projectWithId5),
                Arguments.of(projectId6, projectWithId6),
                Arguments.of(projectId7, projectWithId7)
        );
    }

    private static Stream<Arguments> provideArgumentsForTestValidateProjectExistenceShouldThrowException() {
        return Stream.of(
                Arguments.of(projectId1, null),
                Arguments.of(projectId2, null),
                Arguments.of(projectId3, null),
                Arguments.of(projectId4, null),
                Arguments.of(projectId5, null),
                Arguments.of(projectId6, null),
                Arguments.of(projectId7, null)
        );
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateProjectExistence")
    public void testValidateProjectExistence(long projectId, Project project) {
        when(projectRepository.existsById(projectId))
                .thenReturn(true);

        projectValidator.validateProjectExistence(projectId);
        verify(projectRepository, times(1))
                .existsById(idCaptor.capture());
        var actualProjectId = idCaptor.getValue();

        assertEquals(projectId, actualProjectId);

        verifyNoMoreInteractions(projectRepository);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForTestValidateProjectExistenceShouldThrowException")
    public void testValidateProjectExistenceShouldThrowException(long projectId, Project project) {
        when(projectRepository.existsById(projectId))
                .thenReturn(false);
        DataValidationException actualException = assertThrows(DataValidationException.class,
                () -> projectValidator.validateProjectExistence(projectId));
        verify(projectRepository, times(1))
                .existsById(idCaptor.capture());

        var expectedMessage = String.format("a project with id %d does not exist", projectId);
        var actualMessage = actualException.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(projectRepository);
    }
}
