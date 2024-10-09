package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ProjectValidatorTest {

    @Mock
    ProjectRepository projectRepository;

    @InjectMocks
    ProjectValidator validator;

    @Test
    void testValidateCanceledProjectError(){
        Project project = Project.builder().status(ProjectStatus.CANCELLED).build();

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        assertThrows(DataValidationException.class, () -> validator.validateProject(1L));
    }

    @Test
    void testValidateProjectOk(){
        Project project = Project.builder().status(ProjectStatus.IN_PROGRESS).build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);

        assertEquals(project, validator.validateProject(1L));
    }
}
