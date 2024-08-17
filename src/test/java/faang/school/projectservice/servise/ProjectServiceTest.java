package faang.school.projectservice.servise;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    ProjectUtilService projectRepository;

    @Spy
    ProjectMapperImpl projectMapper;

    @InjectMocks
    ProjectService projectService;

    @Test
    public void testGetProjectIfProjectNotFound() {
        when(projectRepository.getProjectById(anyLong())).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> projectService.getProjectById(anyLong()));
    }

    @Test
    public void testGetProjectSuccessful() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(new Project());

        projectService.getProjectById(anyLong());

        verify(projectRepository, times(1)).getProjectById(anyLong());
    }
}
