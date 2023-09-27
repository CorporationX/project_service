package faang.school.projectservice.validator;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MomentValidatorTest {

    @InjectMocks
    private MomentValidator momentValidator;
    @Mock
    private ProjectService projectService;

    @Test
    public void checkIsProjectClosedTestNullIds() {
        Exception exception = assertThrows(DataValidationException.class,
                () -> momentValidator.isProjectClosed(null));
        assertEquals("null parameter",
                "Moment must be created from at least one project",
                exception.getMessage()
        );
    }
    @Test
    public void checkIsProjectClosedTestEmptyIds() {
        List<Long> ids = new ArrayList<>();

        Exception exception = assertThrows(DataValidationException.class,
                () -> momentValidator.isProjectClosed(ids));
        assertEquals("null parameter",
                "Moment must be created from at least one project",
                exception.getMessage()
        );
    }

    @Test
    public void checkIsProjectClosedTestCancelledProjectThrowsException() {
        Long cancelledProjectId = 1L;
        Long createdProjectId = 2L;
        ProjectDto cancelledProject = ProjectDto.builder().id(cancelledProjectId).status(ProjectStatus.CANCELLED).build();
        ProjectDto createdProject = ProjectDto.builder().id(createdProjectId).status(ProjectStatus.CREATED).build();

        Mockito.when(projectService.getProjectById(cancelledProjectId)).thenReturn(cancelledProject);
        Mockito.when(projectService.getProjectById(createdProjectId)).thenReturn(createdProject);

        Exception exception = assertThrows(DataValidationException.class,
                () -> momentValidator.isProjectClosed(List.of(createdProjectId, cancelledProjectId)));
        assertEquals("Cancelled project",
                "You can't create moment for project: 1 with status CANCELLED",
                exception.getMessage()
        );
    }

    @Test
    public void checkIsProjectClosedTestValid() {
        Long inProgressProjectId = 1L;
        Long createdProjectId = 2L;
        ProjectDto inProgressProject = ProjectDto.builder().id(inProgressProjectId).status(ProjectStatus.IN_PROGRESS).build();
        ProjectDto createdProject = ProjectDto.builder().id(createdProjectId).status(ProjectStatus.CREATED).build();

        Mockito.when(projectService.getProjectById(inProgressProjectId)).thenReturn(inProgressProject);
        Mockito.when(projectService.getProjectById(createdProjectId)).thenReturn(createdProject);

        momentValidator.isProjectClosed(List.of(createdProjectId, inProgressProjectId));
        Mockito.verify(projectService, Mockito.times(2))
                .getProjectById(Mockito.anyLong());
    }
}
