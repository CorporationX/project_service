package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.ProjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private ProjectService projectService;
    @Mock
    private ProjectValidator projectValidator;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private ProjectFilter projectFilter;
    @Mock
    private UserContext userContext;
    @Mock
    private ProjectRepository projectRepository;
    private ProjectFilterDto projectFilterDto;
    private ProjectDto projectDto;
    private Project project;
    private List<Project> projects;
    private List<ProjectDto> projectDtos;

    @BeforeEach
    public void setUp() {
        List<ProjectFilter> projectFilters = List.of(projectFilter);

        projectService = new ProjectService(projectRepository, projectMapper,
                userContext, projectValidator, projectFilters, teamMemberRepository);


        long id = 1L;
        long ownerId = 2L;
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

        lenient().when(projectFilters.get(0).isApplicable(projectFilterDto)).thenReturn(true);
        lenient().when(projectFilters.get(0).filter(any(), any())).thenReturn(Stream.of(project));
    }

    @Test
    void findByIdTest() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.findById(1L);
        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void findAllTest() {
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDtoList(projects)).thenReturn(projectDtos);
        List<ProjectDto> result = projectService.findAll();
        assertNotNull(result);
    }

    @Test
    void createProjectTest() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
        ProjectDto result = projectService.createProject(projectDto);
        assertNotNull(result);
        assertEquals(projectDto, result);
    }

    @Test
    void updateProjectTest() {
        when(projectMapper.toEntity(projectDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);
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
        projectService.getAllProjectByFilters(projectFilterDto);
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findDifferentProjects with new projects not in database")
    void testFindDifferentProjectsWhenNewProjectIdsNotInDatabase() {
        List<Project> projectsFromDataBase = List.of(Project.builder().id(1L).name("Existing Project").build());
        List<Long> newProjectIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L));

        when(projectRepository.getProjectById(2L)).thenReturn(new Project());
        when(projectRepository.getProjectById(3L)).thenReturn(new Project());

        List<Project> result = projectService.findDifferentProjects(projectsFromDataBase, newProjectIds);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with empty newProjectIds list")
    void testFindDifferentProjectsWhenNewProjectIdsIsEmpty() {
        List<Project> projectsFromDataBase = List.of(Project.builder().id(1L).name("Existing Project").build());

        List<Project> result = projectService.findDifferentProjects(projectsFromDataBase, new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with empty projectsFromDataBase list")
    void testFindDifferentProjectsWhenProjectsFromDataBaseIsEmpty() {
        List<Long> newProjectIds = Arrays.asList(1L, 2L, 3L);

        when(projectRepository.getProjectById(1L)).thenReturn(new Project());
        when(projectRepository.getProjectById(2L)).thenReturn(new Project());
        when(projectRepository.getProjectById(3L)).thenReturn(new Project());

        List<Project> result = projectService.findDifferentProjects(new ArrayList<>(), newProjectIds);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with both lists empty")
    void testFindDifferentProjectsWhenBothListsAreEmpty() {
        List<Project> result = projectService.findDifferentProjects(new ArrayList<>(), new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getNewProjects with empty user IDs list")
    void testGetNewProjectsWhenUserIdsIsEmpty() {
        List<Project> result = projectService.getNewProjects(new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test getNewProjects with user IDs having no team members")
    void testGetNewProjectsWhenNoneOfUserIdsHaveTeamMembers() {
        List<Long> userIds = Arrays.asList(1L, 2L);

        when(teamMemberRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(teamMemberRepository.findByUserId(2L)).thenReturn(new ArrayList<>());

        List<Project> result = projectService.getNewProjects(userIds);

        assertEquals(0, result.size());
    }
}