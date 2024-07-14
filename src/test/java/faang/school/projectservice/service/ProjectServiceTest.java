package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private UserContext userContext;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private ProjectFilter projectFilter;

    ProjectDto projectDto;
    Project project;
    List<Project> projects;
    List<ProjectDto> projectDtos;
    ProjectFilterDto projectFilterDto;

    @BeforeEach
    void init() {
        List<ProjectFilter> projectFilters = List.of(projectFilter);
        projectService = new ProjectService(projectRepository, projectMapper, userContext, projectValidator, projectFilters);


        Long id = 1L;
        Long ownerId = 2L;
        String name = "some name";
        LocalDateTime creationDate = LocalDateTime.now();
        ProjectStatus created = ProjectStatus.CREATED;
        ProjectVisibility visibility = ProjectVisibility.PUBLIC;

        projectDto = ProjectDto.builder()
                .id(id)
                .name(name)
                .createdAt(creationDate)
                .updatedAt(creationDate)
                .ownerId(ownerId)
                .status(created)
                .visibility(visibility).build();

        project = Project.builder()
                .id(id)
                .name(name)
                .createdAt(creationDate)
                .updatedAt(creationDate)
                .ownerId(ownerId)
                .status(created)
                .visibility(visibility).build();
        projects = List.of(project);
        projectDtos = List.of(projectDto);
        projectFilterDto = ProjectFilterDto.builder()
                .name("some name")
                .projectStatus(ProjectStatus.CREATED).build();

        when(projectFilters.get(0).isApplicable(projectFilterDto)).thenReturn(true);
        when(projectFilters.get(0).apply(any(), any())).thenReturn(List.of(project).stream());
    }

    @Test
    void findByIdTest() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectMapper.entityToDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.findById(1L);
        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void findAllTest() {
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.entitiesToDtos(projects)).thenReturn(projectDtos);
        List<ProjectDto> result = projectService.findAll();
        assertNotNull(result);
    }

    @Test
    void createProjectTest() {
        when(projectMapper.dtoToEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.entityToDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.createProject(projectDto);
        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void updateProjectTest() {
        when(projectMapper.dtoToEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.entityToDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.createProject(projectDto);
        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void existById() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        projectService.existById(1L);
        verify(projectRepository, times(2)).existsById(1L);
    }

    @Test
    void existByIdNotFoundTest() {
        when(projectRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> projectService.existById(anyLong()));
    }

    @Test
    void getAllProjectByFilters() {
        when(projectRepository.findAll()).thenReturn(projects);
//        не могу понять как оттестить вызов фильтров
        List<ProjectDto> result = projectService.getAllProjectByFilters(projectFilterDto);
//        verify(projectFilter).apply(projects.stream(), projectFilterDto);
    }
}
