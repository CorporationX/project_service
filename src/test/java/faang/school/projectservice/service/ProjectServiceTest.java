package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.project.UpdateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Spy
    private UpdateProjectMapper updateProjectMapper = UpdateProjectMapper.INSTANCE;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void updateTest() {
        Project project = Project.builder()
                .id(1L)
                .status(ProjectStatus.CREATED)
                .description("Default")
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        UpdateProjectDto updateProjectDto =
                new UpdateProjectDto(1L, ProjectStatus.COMPLETED, "NotDefault");

        when(projectRepository.getProjectById(1L)).thenReturn(project);

        UpdateProjectDto result = projectService.update(updateProjectDto);

        assertNotNull(result);
        assertEquals(ProjectStatus.COMPLETED, result.getStatus());
        assertEquals("NotDefault", result.getDescription());
    }
}