package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    private final Long firstId = 1L;
    private final String firstDescription = "common description";
    private final String firstName = "common name";
    private final List<Project> listProjects =
            List.of(createEntity(firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED,
                            Collections.emptyList()),
                    createEntity(firstDescription, ProjectVisibility.PRIVATE, ProjectStatus.CREATED,
                            Collections.emptyList()),
                    createEntity(firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.IN_PROGRESS,
                            Collections.emptyList()),
                    createEntity(firstDescription, ProjectVisibility.PRIVATE, ProjectStatus.IN_PROGRESS,
                            Collections.emptyList())
            );

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectFilter projectNameFilter;

    @Mock
    private ProjectFilter projectStatusFilter;

    @BeforeEach
    public void setUp() {
        projectService = new ProjectService(projectRepository, projectMapper,
                List.of(projectNameFilter, projectStatusFilter));
    }

    @Test
    public void testNegativeCreateWithUserNotFound() {
        assertThrows(NullPointerException.class, () -> projectService.createProject(null,
                createDto(null, null, ProjectVisibility.PUBLIC, ProjectStatus.CREATED)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testNegativeCreateWithEmptyDescription(String description) {
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(firstId,
                createDto(firstName, description, ProjectVisibility.PUBLIC, ProjectStatus.CREATED)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void testNegativeCreateWithEmptyName(String name) {
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(firstId,
                createDto(name, firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED)));
    }

    @Test
    public void testNegativeCreateWithEqualsNames() {
        when(projectRepository.findAll()).thenReturn(List.of(
                createEntity(firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED,
                        Collections.emptyList())));

        assertThrows(IllegalStateException.class, () -> projectService.createProject(firstId,
                createDto(firstName, firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED)));
    }

    @Test
    public void testPositiveCreateSuccessful() {
        Project project = createEntity(firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED,
                null);
        when(projectRepository.findAll()).thenReturn(List.of());

        projectService.createProject(firstId,
                createDto(firstName, firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED));

        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testNegativeUpdateWithProjectNotFound() {
        when(projectRepository.existsById(firstId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> projectService.updateProject(firstId,
                createDto(firstName, firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED)));
    }

    @Test
    public void testPositiveUpdateSuccessful() {
        String secondDescription = "uncommon description";
        Project findedProject =
                createEntity(firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED,
                        Collections.emptyList());
        Project project =
                createEntity(secondDescription, ProjectVisibility.PRIVATE, ProjectStatus.IN_PROGRESS,
                        Collections.emptyList());
        when(projectRepository.existsById(firstId)).thenReturn(true);
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(findedProject));

        projectService.updateProject(firstId,
                createDto(firstName, secondDescription, ProjectVisibility.PRIVATE, ProjectStatus.IN_PROGRESS));

        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testPositiveFindProjectsByFilters() {
        when(projectRepository.findAll()).thenReturn(listProjects);
        when(projectNameFilter.isApplicable(any())).thenReturn(true);
        when(projectStatusFilter.isApplicable(any())).thenReturn(true);
        when(projectNameFilter.apply(any(), any())).thenReturn(listProjects.stream()
                .filter(project -> project.getName().equals(firstName)));
        when(projectStatusFilter.apply(any(), any())).thenReturn(listProjects.stream()
                .filter(project -> project.getStatus() == ProjectStatus.CREATED));

        List<ProjectDto> filteredProjects =
                projectService.findProjectsByFilters(firstId, new ProjectFilterDto(firstName, ProjectStatus.CREATED));

        assertEquals(2, filteredProjects.size());
        assertEquals(ProjectVisibility.PUBLIC, filteredProjects.get(0).visibility());
        assertEquals(ProjectVisibility.PRIVATE, filteredProjects.get(1).visibility());
        assertEquals(ProjectStatus.CREATED, filteredProjects.get(0).status());
        assertEquals(ProjectStatus.CREATED, filteredProjects.get(1).status());
    }

    @Test
    public void testPositiveGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(listProjects);

        List<ProjectDto> foundProjects = projectService.getAllProjects(firstId);

        assertEquals(4, foundProjects.size());
    }

    @Test
    public void testNegativeGetProjectByIdNotFound() {
        assertThrows(NullPointerException.class, () -> projectService.getProjectById(firstId, null));
    }

    @Test
    public void testPositiveGetProjectById() {
        Project project = createEntity(firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED,
                Collections.emptyList());
        when(projectRepository.findById(firstId)).thenReturn(Optional.of(project));

        ProjectDto foundProject = projectService.getProjectById(firstId, firstId);
        ProjectDto expectedProject =
                createDto(firstName, firstDescription, ProjectVisibility.PUBLIC, ProjectStatus.CREATED);

        assertEquals(expectedProject.name(), foundProject.name());
        assertEquals(expectedProject.description(), foundProject.description());
        assertEquals(expectedProject.visibility(), foundProject.visibility());
        assertEquals(expectedProject.status(), foundProject.status());
    }

    @DisplayName("Create project dto for create tests")
    private ProjectDto createDto(String name, String description, ProjectVisibility visibility, ProjectStatus status) {
        return ProjectDto.builder()
                .description(description)
                .name(name)
                .visibility(visibility)
                .status(status)
                .build();
    }

    @DisplayName("Create project entity for create tests")
    private Project createEntity(String description, ProjectVisibility visibility,
                                 ProjectStatus status, List<Team> teams) {
        return Project.builder()
                .description(description)
                .name("common name")
                .ownerId(firstId)
                .visibility(visibility)
                .status(status)
                .teams(teams)
                .build();
    }
}
