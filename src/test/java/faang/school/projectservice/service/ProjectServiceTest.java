package faang.school.projectservice.service;

import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    public void testExistsByIdReturnsTrue() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);

        // Act
        boolean exists = projectService.existsById(projectId);

        // Assert
        assertTrue(exists);
        verify(projectRepository, times(1)).existsById(projectId);
    }

    @Test
    public void testExistsByIdReturnsFalse() {
        // Arrange
        Long projectId = 2L;
        when(projectRepository.existsById(projectId)).thenReturn(false);

        // Act
        boolean exists = projectService.existsById(projectId);

        // Assert
        assertFalse(exists);
        verify(projectRepository, times(1)).existsById(projectId);
    }
}
