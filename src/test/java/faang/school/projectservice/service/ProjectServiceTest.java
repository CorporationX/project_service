package faang.school.projectservice.service;

import faang.school.projectservice.config.amazon.ResourceConfig;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.service.imageprocessing.ImageProcessingUtils;
import faang.school.projectservice.validator.project.FileValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    private ProjectService projectService;
    private S3Service s3Service;
    private ProjectMapper projectMapper;
    private FileValidator validator;
    private ImageProcessingUtils imageProcessingUtils;

    private final Long projectId = 1L;
    private final Project project = Project.builder().id(projectId).build();

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository,
                s3Service,
                projectMapper,
                validator,
                imageProcessingUtils);
    }

    @Test
    public void shouldSuccessGetProject() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        Project result = projectService.getProject(projectId);
        assertEquals(project, result);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionIfProjectNotExists() {
        when(projectRepository.findById(anyLong())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> projectService.getProject(projectId));
    }

    @Test
    public void shouldSuccessGetProjects() {
        List<Project> expectedProjects = List.of(project);
        when(projectRepository.findAllById(anyList())).thenReturn(expectedProjects);

        List<Project> result = projectRepository.findAllById(List.of(projectId));
        assertEquals(expectedProjects, result);
    }

    @Test
    public void shouldReturnsEmptyListIfProjectsAreNotExist() {
        when(projectRepository.findAllById(anyList())).thenReturn(List.of());

        List<Project> result = projectRepository.findAllById(List.of(projectId));
        assertTrue(result.isEmpty());
    }
}