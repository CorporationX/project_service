package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    @Spy
    private ProjectMapper mockProjectMapper = new ProjectMapperImpl();
    @Mock
    private ProjectRepository projectRepository;

    ProjectDto projectDto;
    Project project;

    @BeforeEach
    void setUp() {
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .build();
        LocalDateTime now = LocalDateTime.now();
        project = Project.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void testCreateProject() {
        projectDto.setName("Project^%$^*^^£     C++, Python/C# Мой проект.    ");
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectStatus.CREATED, projectService.create(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals("project c++, python/c# мой проект.", projectService.create(projectDto).getName());
    }

    @Test
    void testCreateProjectThrowsException() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        DataAlreadyExistingException dataAlreadyExistingException = Assertions
                .assertThrows(DataAlreadyExistingException.class, () -> projectService.create(projectDto));
        Assertions.assertEquals(String.format("User with id: %d already exist project %s",
                projectDto.getOwnerId(), projectDto.getName()), dataAlreadyExistingException.getMessage());
    }

    @Test
    void testUpdateStatus() {
        long projectId = 1L;
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto, projectId).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals(project.getDescription(), projectService.update(projectDto, projectId).getDescription());
    }

    @Test
    void testUpdateDescription() {
        long projectId = 1L;
        projectDto.setDescription("New Description");
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals("New Description", projectService.update(projectDto, projectId).getDescription());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals(project.getStatus(), projectService.update(projectDto, projectId).getStatus());
    }

    @Test
    void testUpdateStatusAndDescription() {
        long projectId = 1L;
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        projectDto.setDescription("New Description");
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto, projectId).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals("New Description", projectService.update(projectDto, projectId).getDescription());
    }
}