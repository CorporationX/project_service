package faang.school.projectservice.service;

import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    @Mock
    private ResourceValidator resourceValidator;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private AmazonS3Service amazonS3Client;

    @Spy
    private ResourceMapper resourceMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ProjectService projectService;

    private final Long projectId = 1L;
    private final Project project = Project.builder().id(projectId).build();

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