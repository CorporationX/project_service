package faang.school.projectservice.validation;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidationTest {

    private final long PROJECT_ID = 1L;
    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectValidation projectValidation;

    @Test
    public void testCheckProjectExists() {
        when(projectRepository.existsById(PROJECT_ID)).thenReturn(false);
        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> projectValidation.checkProjectExists(PROJECT_ID));
        assertEquals("Project with projectId=" + PROJECT_ID + " not exist",
                dataValidationException.getMessage());
    }

    @Test
    public void testCheckProjectsStatusesInCompleted() {
        Project project = Project.builder()
                .id(PROJECT_ID)
                .status(ProjectStatus.COMPLETED)
                .build();

        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);

        DataValidationException dataValidationException = assertThrows(
                DataValidationException.class,
                () -> projectValidation.checkProjectStatuses(PROJECT_ID));
        assertEquals("It is impossible to create a moment because the project " +
                        "is in status completed",
                dataValidationException.getMessage());
    }
}