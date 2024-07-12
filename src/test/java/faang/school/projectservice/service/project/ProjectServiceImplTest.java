package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper mapper;

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
    void createProject_returns_project_dto_if_project_does_not_exist() {
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

        assertEquals(projectDto, createdProjectDto);
        assertEquals(ProjectStatus.CREATED, createdProjectDto.getStatus());
    }

}