package faang.school.projectservice;

import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.adapter.project.ProjectRepoAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectRepoAdapterTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectRepoAdapter projectRepoAdapter;

    @Test
    void testProjectFromRepository_WhenProjectExists_ShouldReturnProject() {
        long projectId = 1L;
        Project expectedProject = new Project();
        expectedProject.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(expectedProject));

        Project actualProject = projectRepoAdapter.getProjectById(projectId);

        assertNotNull(actualProject);
        assertEquals(expectedProject, actualProject);
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testProjectFromRepository_WhenProjectDoesNotExist_ShouldThrowException() {
        long nonExistentProjectId = 999L;
        String expectedMessage = "Project with id=" + nonExistentProjectId + " not found";

        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(
                ProjectNotFoundException.class,
                () -> projectRepoAdapter.getProjectById(nonExistentProjectId)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(projectRepository, times(1)).findById(nonExistentProjectId);
    }
}
