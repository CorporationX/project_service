package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.IllegalSubProjectsStatusException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.config.context.UserContext;
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

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    @Mock
    private ProjectValidator projectValidator;


    @Mock
    private ProjectRepository projectRepository;

    private ProjectService projectService;

    private CreateSubProjectDto createSubProjectDto;
    private Project parentProject;
    private List<Project> subProjects;
    private ProjectMapperImpl projectMapperImpl;
    private ProjectFilterDto projectFilterDto;

    @BeforeEach
    public void setUp() {
        List<ProjectFilter> projectFilterList = List.of(
                new ProjectNameFilter(),
                new ProjectStatusFilter()
        );

        projectMapperImpl = new ProjectMapperImpl();

        projectService = new ProjectService(projectMapperImpl, projectValidator,
                projectRepository, projectFilterList);

        subProjects = List.of(
                Project.builder()
                        .name("ProjectName")
                        .status(ProjectStatus.IN_PROGRESS)
                        .visibility(ProjectVisibility.PUBLIC)
                        .children(new ArrayList<>()).build(),
                Project.builder()
                        .name("not")
                        .status(ProjectStatus.IN_PROGRESS)
                        .visibility(ProjectVisibility.PUBLIC)
                        .children(new ArrayList<>()).build()
        );

        parentProject = Project.builder()
                .id(1L)
                .children(subProjects)
                .build();

        createSubProjectDto = CreateSubProjectDto.builder()
                .parentProjectId(parentProject.getId())
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectFilterDto = ProjectFilterDto.builder()
                .name("ProjectName")
                .projectStatus(ProjectStatus.IN_PROGRESS)
                .build();

        lenient().when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
    }

    @Test
    @DisplayName("testing createSubProject")
    public void testCreateSubProjectWithProjectRepositoryMethodExecution() {
        projectService.createSubProject(createSubProjectDto);
        verify(projectRepository, times(1))
                .getProjectById(createSubProjectDto.getParentProjectId());
        verify(projectValidator, times(1))
                .validateSubProjectVisibility(parentProject.getVisibility(), createSubProjectDto.getVisibility());
        verify(projectRepository, times(1)).save(projectArgumentCaptor.capture());
    }

    @Test
    @DisplayName("testing updateSubProject with non appropriate Status")
    public void testUpdateSubProjectWithNonAppropriateStatus() {
        ProjectDto projectDto = ProjectDto.builder()
                .status(ProjectStatus.COMPLETED).build();
        when(projectRepository.getAllSubProjectsFor(parentProject.getId())).thenReturn(parentProject.getChildren());
        assertThrows(IllegalSubProjectsStatusException.class,
                () -> projectService.updateProject(parentProject.getId(), projectDto));
    }

    @Test
    @DisplayName("testing updateSubProject with changing visibility to private and status to completed")
    public void testUpdateSubProjectWithVisibilityPrivateChangeStatusCompleted() {
        ProjectDto projectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.COMPLETED).build();
        when(projectRepository.getAllSubProjectsFor(parentProject.getId())).thenReturn(parentProject.getChildren());
        parentProject.setVisibility(ProjectVisibility.PUBLIC);
        parentProject.getChildren().forEach(project -> {
            project.setVisibility(ProjectVisibility.PUBLIC);
            project.setStatus(ProjectStatus.COMPLETED);
        });

        projectService.updateProject(parentProject.getId(), projectDto);
        assertEquals(ProjectVisibility.PRIVATE, parentProject.getVisibility());
        assertEquals(ProjectVisibility.PRIVATE, parentProject.getChildren().get(0).getVisibility());
        assertEquals(ProjectStatus.COMPLETED, parentProject.getStatus());
    }

    @Test
    @DisplayName("testing updateSubProject with visibility to public changing and status to cancelled")
    public void testUpdateSubProjectWithVisibilityPublicStatusCancelled() {
        ProjectDto projectDto = ProjectDto.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CANCELLED).build();
        parentProject.setVisibility(ProjectVisibility.PRIVATE);
        parentProject.getChildren().forEach(project -> {
            project.setVisibility(ProjectVisibility.PRIVATE);
            project.setStatus(ProjectStatus.CANCELLED);
        });

        projectService.updateProject(parentProject.getId(), projectDto);
        assertEquals(parentProject.getVisibility(), ProjectVisibility.PUBLIC);
        assertEquals(parentProject.getChildren().get(0).getVisibility(), ProjectVisibility.PRIVATE);
        assertEquals(parentProject.getStatus(), ProjectStatus.CANCELLED);
    }

    @Test
    @DisplayName("testing getSubProjects with selection correct subProject")
    public void testGetSubProjects() {
        List<ProjectDto> selectedSubProjects = projectService.getSubProjects(parentProject.getId(), projectFilterDto);
        assertEquals(1, selectedSubProjects.size());
        assertEquals(projectMapperImpl.toDto(subProjects.get(0)), selectedSubProjects.get(0));
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ProjectFilter projectFilter;

    @Mock
    private UserContext userContext;

    @Mock
    private ProjectValidator projectValidator;


    ProjectDto projectDto;
    Project project;
    List<Project> projects;
    List<ProjectDto> projectDtos;
    ProjectFilterDto projectFilterDto;
    List<Project> projectsFromDataBase;
    List<Long> newProjectIds;
    List<Long> userIds;

    @BeforeEach
    void init() {
        List<ProjectFilter> projectFilters = List.of(projectFilter);
        projectService = new ProjectService(projectRepository, projectMapper,
                userContext, projectValidator, projectFilters, teamMemberRepository);


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

        Mockito.lenient().when(projectFilters.get(0).isApplicable(projectFilterDto)).thenReturn(true);
        Mockito.lenient().when(projectFilters.get(0).apply(any(), any())).thenReturn(List.of(project).stream());

        projectsFromDataBase = new ArrayList<>();
        newProjectIds = new ArrayList<>();
        userIds = new ArrayList<>();
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
        projectService.getAllProjectByFilters(projectFilterDto);
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findDifferentProjects with new projects not in database")
    void testFindDifferentProjectsWhenNewProjectIdsNotInDatabase() {
        projectsFromDataBase.add(Project.builder().id(1L).name("Existing Project").build());
        newProjectIds = new ArrayList<>(Arrays.asList(1L, 2L, 3L));

        when(projectRepository.getProjectById(2L)).thenReturn(new Project());
        when(projectRepository.getProjectById(3L)).thenReturn(new Project());

        List<Project> result = projectService.findDifferentProjects(projectsFromDataBase, newProjectIds);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with empty newProjectIds list")
    void testFindDifferentProjectsWhenNewProjectIdsIsEmpty() {
        projectsFromDataBase.add(Project.builder().id(1L).name("Existing Project").build());

        List<Project> result = projectService.findDifferentProjects(projectsFromDataBase, new ArrayList<>());

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test findDifferentProjects with empty projectsFromDataBase list")
    void testFindDifferentProjectsWhenProjectsFromDataBaseIsEmpty() {
        newProjectIds = Arrays.asList(1L, 2L, 3L);

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
        userIds = Arrays.asList(1L, 2L);

        when(teamMemberRepository.findByUserId(1L)).thenReturn(new ArrayList<>());
        when(teamMemberRepository.findByUserId(2L)).thenReturn(new ArrayList<>());

        List<Project> result = projectService.getNewProjects(userIds);

        assertEquals(0, result.size());
    }
}