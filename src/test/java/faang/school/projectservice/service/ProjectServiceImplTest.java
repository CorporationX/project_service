package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.SubProjectValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SubProjectValidation validation;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;

    long userId = 1L;
    long projectId = 2L;
    long ownerId = 1L;
    long parentId = 2L;

    @Test
    void createSubProject_validInput() {
        CreateSubProjectDto createSubProjectDto = new CreateSubProjectDto(null, "Test subproject", "Description", parentId);
        Project parentProject = Project.builder().id(parentId).build();
        Project subProject = Project.builder()
                .id(3L)
                .name("Test subproject")
                .description("Description")
                .ownerId(ownerId)
                .parentProject(Project.builder().id(parentId).build())
                .build();

        when(projectRepository.getProjectById(parentId)).thenReturn(parentProject);
        when(projectMapper.toEntity(createSubProjectDto, parentProject, ownerId)).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(projectMapper.toDto(subProject)).thenReturn(ProjectDto.builder().id(3L)
                .name("Test subproject")
                .description("Description")
                .ownerId(ownerId)
                .parentId(parentId)
                .build());

        ProjectDto result = projectServiceImpl.createSubProject(ownerId, createSubProjectDto);

        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.name()).isEqualTo("Test subproject");
        assertThat(result.description()).isEqualTo("Description");
        assertThat(result.ownerId()).isEqualTo(ownerId);
        assertThat(result.parentId()).isEqualTo(parentId);

        verify(projectRepository, times(1)).getProjectById(parentId);
        verify(projectMapper, times(1)).toEntity(createSubProjectDto, parentProject, ownerId);
        verify(projectRepository, times(1)).save(subProject);
        verify(projectMapper, times(1)).toDto(subProject);
    }

    @Test
    void updateSubProject_validInput() {
        ProjectStatus status = ProjectStatus.IN_PROGRESS;
        ProjectVisibility visibility = ProjectVisibility.PRIVATE;
        UpdateSubProjectDto updateSubProjectDto = new UpdateSubProjectDto(projectId, status, visibility);
        Project project = Project.builder()
                .id(projectId)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project updatedProject = Project
                .builder()
                .id(projectId)
                .status(status)
                .visibility(visibility)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.save(any())).thenReturn(any());
        when(projectMapper.toDto(updatedProject)).thenReturn(ProjectDto.builder().id(projectId)
                .status(status)
                .build());
        ProjectDto result = projectServiceImpl.updateSubProject(userId, updateSubProjectDto);

        assertThat(result.id()).isEqualTo(projectId);
        assertThat(result.status()).isEqualTo(status);

        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(validation, times(1)).updateSubProject(userId, updateSubProjectDto, project);
        verify(projectRepository, times(1)).save(any());
        verify(projectMapper, times(1)).toDto(any());
    }
}