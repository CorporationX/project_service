package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
    }

    @Test
    void findEntityById_ShouldReturnProject() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // When
        Project foundProject = projectService.findEntityById(1L);

        // Then
        assertNotNull(foundProject);
        assertEquals(1L, foundProject.getId());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void findEntityById_ShouldThrowExceptionWhenProjectNotFound() {
        // Given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> projectService.findEntityById(1L));
        verify(projectRepository, times(1)).findById(1L);
    }
}
