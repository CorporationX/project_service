package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.ProjectStatusException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectValidatorTest {
    private final long projectId = 1;

    @InjectMocks
    private ProjectValidator projectValidator;

    @Mock
    private ProjectRepository projectRepository;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
    }

    @Test
    void testValidateProjectNotCancelled() {
        project.setStatus(ProjectStatus.CANCELLED);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertThrows(ProjectStatusException.class, () -> projectValidator.validateProjectNotCancelled(projectId));
    }

    @Test
    void testValidateProjectNotCompleted() {
        project.setStatus(ProjectStatus.COMPLETED);

        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertThrows(ProjectStatusException.class, () -> projectValidator.validateProjectNotCompleted(projectId));
    }
}