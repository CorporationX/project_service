package faang.school.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private List<ProjectFilter> projectFilters;

    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        projectDto = Mockito.mock(ProjectDto.class);
    }

    @Test
    public void testCreateProjectWhenValidInput() {
        String name = "name";
        String description = "description";
        when(projectDto.getProjectDescription()).thenReturn(description);
        when(projectDto.getProjectName()).thenReturn(name);

        when(projectMapper.toProject(projectDto)).thenReturn(new Project());
        when(projectMapper.toProjectDto(any())).thenReturn(projectDto);
        when(projectRepository.existsByOwnerUserIdAndName(any(), any())).thenReturn(false);
        when(projectRepository.save(any())).thenReturn(new Project());

        ProjectDto savedProject = projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(any());
        verify(projectMapper, times(1)).toProjectDto(any());
        assertNotNull(savedProject);
    }

    @Test
    void testCreateProjectWhenProjectExists() {
        String name = "name";
        String description = "description";
        when(projectDto.getProjectDescription()).thenReturn(description);
        when(projectDto.getProjectName()).thenReturn(name);

        when(projectRepository.existsByOwnerUserIdAndName(any(), any())).thenReturn(true);

        assertThrows(ValidationException.class, () -> projectService.createProject(projectDto));
    }

    @Test
    void testUpdateProjectWhenValidInput() {
        long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        String name = "name";
        String description = "description";
        when(projectDto.getProjectDescription()).thenReturn(description);
        when(projectDto.getProjectName()).thenReturn(name);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectMapper.toProjectDto(any())).thenReturn(projectDto);

        ProjectDto updatedProject = projectService.updateProject(projectId, projectDto);

        verify(projectRepository, times(1)).save(project);
        assertNotNull(updatedProject);
    }

    @Test
    void testUpdateProjectWhenProjectDoesNotExist() {
        long projectId = 1L;
        String name = "name";
        String description = "description";
        when(projectDto.getProjectDescription()).thenReturn(description);
        when(projectDto.getProjectName()).thenReturn(name);
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> projectService.updateProject(projectId, projectDto));
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = List.of(new Project(), new Project());
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toProjectDtos(projects)).thenReturn(Collections.singletonList(projectDto));

        List<ProjectDto> projectDtos = projectService.getAllProjects();

        assertEquals(1, projectDtos.size());
    }

    @Test
    void testGetAllProjectsByFilterWhenFiltersAreApplied() {
        Project project = new Project();
        project.setName("Test Project");

        ProjectFilterDto filterDto = new ProjectFilterDto();

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        List<ProjectDto> filteredProjects = projectService.getAllProjectsByFilter(filterDto);

        assertEquals(1, filteredProjects.size());
        assertEquals(projectDto, filteredProjects.get(0));
    }
}
