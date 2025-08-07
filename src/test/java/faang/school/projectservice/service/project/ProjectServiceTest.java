package faang.school.projectservice.service.project;

import faang.school.projectservice.exeption.EntityNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private static final Long PROJECT_ID = 1L;
    private static final String PROJECT_NAME = "Test Project";
    private static final String SAVED_PROJECT_NAME = "Saved Project";

    @Test
    @DisplayName("Should return project by ID if exists")
    void shouldReturnProjectById() {
        Project project = buildProject(PROJECT_NAME);

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        Project result = projectService.getProjectById(PROJECT_ID);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        assertEquals(PROJECT_NAME, result.getName());
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    @DisplayName("Should throw exception if project not found")
    void shouldThrowIfProjectNotFound() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> projectService.getProjectById(PROJECT_ID));

        assertEquals("Project not found", exception.getMessage());
        verify(projectRepository).findById(PROJECT_ID);
    }

    @Test
    @DisplayName("Should save and return project")
    void shouldSaveProject() {
        Project project = buildProject(SAVED_PROJECT_NAME);

        when(projectRepository.save(project)).thenReturn(project);
        Project result = projectService.save(project);

        assertNotNull(result);
        assertEquals(PROJECT_ID, result.getId());
        assertEquals(SAVED_PROJECT_NAME, result.getName());
        verify(projectRepository).save(project);
    }

    private Project buildProject(String name) {
        return Project.builder()
                .id(PROJECT_ID)
                .name(name)
                .build();
    }
}
