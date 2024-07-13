package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper mapper;

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    void createProject_throws_exception_if_project_already_exists() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> projectService.createProject(projectDto));

        assertEquals(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID, exception.getMessage());
    }

    @Test
    void createProject_throws_exception_if_database_constraints_fail() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        var project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(PersistenceException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    void createProject_persists_project_successfully_with_no_parent_project() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        var project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto createdProjectDto = projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
        assertEquals(projectDto, createdProjectDto);
        assertEquals(ProjectStatus.CREATED, projectArgumentCaptor.getValue().getStatus());
    }

    @Test
    void createProject_persists_project_successfully_with_parent_project() {
        var projectDto = ProjectDto.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .parentProjectId(2L)
                .build();

        var parentProject = Project.builder()
                .id(2L)
                .name("Parent project")
                .build();

        var project = Project.builder()
                .name("Test Project")
                .description("Test Project Description")
                .ownerId(1L)
                .parentProject(parentProject)
                .build();

        when(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())).thenReturn(false);
        when(mapper.toEntity(projectDto)).thenReturn(project);
        when(mapper.toDto(project)).thenReturn(projectDto);
        when(projectRepository.getProjectById(projectDto.getParentProjectId())).thenReturn(parentProject);
        when(projectRepository.save(project)).thenReturn(project);

        ProjectDto createdProjectDto = projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
        assertEquals(projectDto, createdProjectDto);
        assertEquals(ProjectStatus.CREATED, projectArgumentCaptor.getValue().getStatus());
        assertEquals(parentProject.getId(), createdProjectDto.getParentProjectId());
    }

}