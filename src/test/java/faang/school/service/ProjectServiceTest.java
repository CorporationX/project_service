package faang.school.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.ValidationException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
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
        Project project = new Project();

        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectDto.getProjectName()).thenReturn("Name");
        when(projectDto.getProjectDescription()).thenReturn("Description");
        when(projectRepository.existsByOwnerUserIdAndName(any(), any())).thenReturn(false);
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toProjectDto(any())).thenReturn(projectDto);

        ProjectDto savedProject = projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(any());
        verify(projectMapper, times(1)).toProjectDto(any());
        assertNotNull(project.getCreatedAt());
        assertNotNull(project.getUpdatedAt());
        assertNotNull(savedProject);
        assertEquals("Name", savedProject.getProjectName());
    }

    @Test
    void testCreateProjectWhenProjectExists() {
        String name = "Name";
        String description = "Description";
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
        project.setName("Name");
        project.setDescription("Old description");
        project.setStatus(ProjectStatus.IN_PROGRESS);
        when(projectDto.getProjectDescription()).thenReturn("New description");
        when(projectDto.getProjectStatus()).thenReturn(ProjectStatus.COMPLETED);
        when(projectDto.getProjectName()).thenReturn(project.getName());
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectMapper.toProject(projectDto)).thenReturn(project);
        when(projectMapper.toProjectDto(any())).thenReturn(projectDto);

        ProjectDto updatedProject = projectService.updateProject(projectId, projectDto);

        verify(projectRepository, times(1)).save(project);
        verify(projectDto, times(1)).getProjectUpdatedAt();
        assertNotNull(updatedProject);
        assertEquals(ProjectStatus.COMPLETED, updatedProject.getProjectStatus());
        assertEquals("New description", updatedProject.getProjectDescription());
    }

    @Test
    void testUpdateProjectWhenProjectDoesNotExist() {
        long projectId = 1L;
        String name = "Name";
        String description = "Description";
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
    public void testGetAllProjectsWithNoFilters() {
        Project project1 = new Project();
        Project project2 = new Project();

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        when(projectFilters.stream()).thenReturn(Stream.empty());

        ProjectDto projectDto1 = new ProjectDto();
        ProjectDto projectDto2 = new ProjectDto();
        when(projectMapper.toProjectDto(project1)).thenReturn(projectDto1);
        when(projectMapper.toProjectDto(project2)).thenReturn(projectDto2);

        List<ProjectDto> result = projectService.getAllProjectsByFilter(new ProjectFilterDto());

        assertEquals(2, result.size());
        assertEquals(projectDto1, result.get(0));
        assertEquals(projectDto2, result.get(1));
    }

    @Test
    void testGetAllProjectsByFilterWithApplicableFilters() {
        Project project = new Project();
        project.setName("Name");

        ProjectFilterDto filterDto = new ProjectFilterDto();

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toProjectDto(project)).thenReturn(projectDto);

        List<ProjectDto> filteredProjects = projectService.getAllProjectsByFilter(filterDto);

        assertEquals(1, filteredProjects.size());
        assertEquals(projectDto, filteredProjects.get(0));
    }

    @Test
    public void testGetAllProjectsByFilterWithNoApplicableFilters() {
        Project project = new Project();
        project.setName("Name");

        ProjectFilterDto filterDto = new ProjectFilterDto();

        when(projectRepository.findAll()).thenReturn(List.of(project));

        ProjectFilter projectFilter = Mockito.mock(ProjectFilter.class);
        when(projectFilter.isApplicable(filterDto)).thenReturn(false);
        when(projectFilters.stream()).thenReturn(Stream.of(projectFilter));

        List<ProjectDto> result = projectService.getAllProjectsByFilter(filterDto);

        assertEquals(1, result.size());
    }
}
