package faang.school.projectservice;

import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectRepositoryAdapterTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Test
    void testProjectFromRepository_WhenProjectExists_ShouldReturnProject() {
        long projectId = 1L;
        Project expectedProject = new Project();
        expectedProject.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(expectedProject));

        Project actualProject = projectRepositoryAdapter.getProjectById(projectId);

        assertNotNull(actualProject);
        assertEquals(expectedProject, actualProject);
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void testProjectFromRepository_WhenProjectDoesNotExist_ShouldThrowException() {
        long nonExistentProjectId = 999L;
        String expectedMessage = "Project with id " + nonExistentProjectId + " not found";

        when(projectRepository.findById(nonExistentProjectId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectRepositoryAdapter.getProjectById(nonExistentProjectId)
        );

        assertEquals(expectedMessage, exception.getMessage());
        verify(projectRepository, times(1)).findById(nonExistentProjectId);
    }
}
