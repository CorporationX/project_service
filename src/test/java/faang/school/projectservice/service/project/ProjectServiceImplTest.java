package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectJpaRepository projectRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void shouldCreateProjectSuccessfullyWhenGetValidProjectDto() {
        ProjectDto projectDto = ProjectDto.builder()
                .name("Test Project")
                .build();
        Project project = Project.builder()
                .ownerId(1L)
                .name("Test Project")
                .status(ProjectStatus.CREATED)
                .build();
        ProjectDto expectedProjectDto = ProjectDto.builder()
                .ownerId(1L)
                .name("Test Project")
                .status(ProjectStatus.CREATED)
                .build();

        when(userContext.getUserId()).thenReturn(1L);
        when(projectRepository.existsByOwnerIdAndName(1L, "Test Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectDto createdProjectDto = projectService.create(projectDto);

        verify(projectRepository).save(project);
        assertEquals(expectedProjectDto, createdProjectDto);
    }

}