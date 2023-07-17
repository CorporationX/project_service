package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;
    ProjectDto projectDto;
    @BeforeEach
    public void init() {
       projectDto = ProjectDto.builder().id(1L).privateProject(true).createdAt(LocalDateTime.now()).description("s").name("q").ownerId(1L).build();
    }
    @Test
    public void testCreateProjectThrowsException() {
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    public void testCreateProject() {
        Project project = Project.builder().id(1L).createdAt(LocalDateTime.now()).description("s").name("q").build();
        ProjectDto projectDto1 = ProjectDto.builder().id(1L).privateProject(true).createdAt(LocalDateTime.now()).description("s").name("q").ownerId(1L).status(ProjectStatus.CREATED).build();
        Mockito.when(projectRepository.existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenReturn(project);
        Mockito.when(projectMapper.toProject(projectDto)).thenReturn(project);
        Mockito.when(projectMapper.toProjectDto(project)).thenReturn(projectDto1);
        assertEquals(ProjectStatus.CREATED, projectService.createProject(projectDto).getStatus());
    }
}