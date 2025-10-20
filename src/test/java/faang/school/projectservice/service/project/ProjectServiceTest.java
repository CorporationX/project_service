package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.client.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectValidator projectValidator;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Test description")
                .ownerId(10L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Test Project")
                .description("Test description")
                .ownerId(10L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    @Test
    void createProject_success() {
        Long ownerId = 10L;

        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.createProject(projectDto, ownerId);

        verify(projectValidator).validateUniqueProjectNameForOwner(projectDto.name(), ownerId);
        verify(projectRepository).save(any(Project.class));
        assertThat(result).isEqualTo(projectDto);
    }

    @Test
    void updateProject_success() {
        Project updatedProject = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Updated description")
                .ownerId(10L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .createdAt(project.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        ProjectDto updateDto = ProjectDto.builder()
                .id(1L)
                .description("Updated description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        when(projectMapper.toDto(updatedProject)).thenReturn(updateDto);

        ProjectDto result = projectService.updateProject(1L, updateDto);

        verify(projectValidator).validateUpdate(project, updateDto.status(), updateDto.description());
        verify(projectRepository).save(project);
        assertThat(result.status()).isEqualTo(ProjectStatus.IN_PROGRESS);
        assertThat(result.description()).isEqualTo("Updated description");
    }

    @Test
    void getAllProjects_success() {
        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getAllProjects();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Test Project");
    }

    @Test
    void getProjectById_success() {
        Long userId = 10L;
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.getProjectById(1L, userId);

        verify(projectValidator).validateAccessToProject(project, userId);
        assertThat(result).isEqualTo(projectDto);
    }

    @Test
    void getProjectsByFilter_withNameAndStatus() {
        Project project2 = Project.builder()
                .id(2L)
                .name("Another Project")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        when(projectRepository.findAll()).thenReturn(List.of(project, project2));
        when(projectMapper.toDto(any(Project.class))).thenReturn(projectDto);

        List<ProjectDto> result = projectService.getProjectsByFilter("Test", ProjectStatus.CREATED, 10L);

        assertThat(result).hasSize(1);
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectById_notFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(999L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Project not found");
    }
}