package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.exception.StatusException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.filter.project.ProjectNameFilter;
import faang.school.projectservice.filter.project.ProjectStatusFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubProjectServiceTest {
    @Mock
    ProjectRepository projectRepository;
    @Mock
    SubProjectMapper subProjectMapper;
    @Mock
    MomentRepository momentRepository;
    @InjectMocks
    SubProjectService subProjectService;
    private SubProjectDto subProjectDto;
    private SubProjectDto subProjectDtoForCreate;
    private Project subProjectForCreate;
    private Project subProjectForUpdate;
    private SubProjectDto subProjectDtoForUpdate;

    static Stream<ProjectFilter> argsProvider1() {
        return Stream.of(
                new ProjectNameFilter(),
                new ProjectStatusFilter()
        );
    }
    static Stream<Arguments> argsProvider2() {
        return Stream.of(
                Arguments.of(new ProjectNameFilter(), ProjectFilterDto.builder().namePattern("r").build()),
                Arguments.of(new ProjectStatusFilter(), ProjectFilterDto.builder().statuses(List.of(ProjectStatus.COMPLETED)).build())
        );
    }

    @BeforeEach
    void setUp() {
        subProjectDto = SubProjectDto.builder().id(2L).name("project").description("desc").parentId(1L)
                .childrenId(List.of(3L, 4L, 5L)).status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PUBLIC).build();
        subProjectDtoForCreate = SubProjectDto.builder().id(111L).name("new subproject").description("some desc").parentId(6L).visibility(ProjectVisibility.PUBLIC).build();
        subProjectForCreate = Project.builder().id(111L).name("new subproject").description("some desc").build();

        subProjectForUpdate = Project.builder().id(66L).name("some project").description("some desc")
                .parentProject(Project.builder().id(77L).build())
                .children(List.of(
                        Project.builder().id(101L).status(ProjectStatus.IN_PROGRESS).visibility(ProjectVisibility.PRIVATE).build(),
                        Project.builder().id(102L).status(ProjectStatus.COMPLETED).visibility(ProjectVisibility.PUBLIC).build(),
                        Project.builder().id(103L).status(ProjectStatus.COMPLETED).visibility(ProjectVisibility.PRIVATE).build()

                ))
                .teams(List.of(Team.builder().id(1L)
                        .teamMembers(List.of(TeamMember.builder().id(123L).userId(123123L).build())).build()))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC).build();
        subProjectDtoForUpdate = SubProjectDto.builder().id(66L).name("some project").description("some desc")
                .parentId(1L).childrenId(List.of(101L, 102L)).status(ProjectStatus.COMPLETED).visibility(ProjectVisibility.PUBLIC).build();
    }

    @Test
    public void testParentProjectNotExist() {
        when(projectRepository.existsById(anyLong())).thenReturn(false);
        DataValidException exception = assertThrows(DataValidException.class,
                () -> subProjectService.createSubProject(subProjectDto));
        assertEquals("No such parent project", exception.getMessage());
    }

    @Test
    public void testVisibilityConsistence() {
        when(projectRepository.existsById(anyLong())).thenReturn(true);
        Project projectVisibility = Project.builder().id(6L).visibility(ProjectVisibility.PRIVATE).build();
        when(projectRepository.getProjectById(anyLong())).thenReturn(projectVisibility);
        DataValidException exception = assertThrows(DataValidException.class,
                () -> subProjectService.createSubProject(subProjectDto));
        assertEquals("The visibility of the subproject must be " +
                ProjectVisibility.PRIVATE + " like the parent project", exception.getMessage());
    }

    @Test
    public void testUniqueSubProject() {
        List<Project> children = List.of(
                Project.builder().id(11L).name("sdfsd").build(),
                Project.builder().id(12L).name("werwer").build(),
                Project.builder().id(13L).name("project").build()
        );
        Project projectRepo = Project.builder().id(6L).visibility(ProjectVisibility.PUBLIC).children(children).build();

        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(anyLong())).thenReturn(projectRepo);

        DataValidException exception = assertThrows(DataValidException.class,
                () -> subProjectService.createSubProject(subProjectDto));
        assertEquals("Subproject with name " + subProjectDto.getName() + " already exists", exception.getMessage());
    }

    @Test
    void createSubProjectTest() {
        SubProjectDto projectDto = createSubProjectDto("subproject", "new subproject", 2L, List.of(10L), ProjectVisibility.PUBLIC, ProjectStatus.CREATED);
        Project project = createProject(2L, "subproject", ProjectVisibility.PUBLIC, new ArrayList<>());
        List<Project> children = List.of(createProject(10L, "childProject", ProjectVisibility.PUBLIC, new ArrayList<>()));
        Project projectFromDto = createProject(null, "subproject", ProjectVisibility.PUBLIC, children);
        SubProjectDto resultDto = createSubProjectDto("subproject", "new subproject", 2L, List.of(10L), ProjectVisibility.PUBLIC, ProjectStatus.CREATED);

        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.getProjectById(2L)).thenReturn(project);
        when(subProjectMapper.toEntity(projectDto)).thenReturn(projectFromDto);
        when(subProjectMapper.toDto(projectFromDto)).thenReturn(projectDto);

        SubProjectDto result = subProjectService.createSubProject(projectDto);

        assertEquals(projectDto, result);
    }

    @Test
    void checkStatusesOfChildrenTest() {
        when(projectRepository.getProjectById(anyLong())).thenReturn(subProjectForUpdate);
        StatusException exception = assertThrows(StatusException.class,
                () -> subProjectService.updateSubProject(subProjectDto));
        assertEquals("All subprojects of this project should have the same status", exception.getMessage());
    }

    @Test
    void updateSubProjectTest() {
        subProjectForUpdate.setVisibility(subProjectDtoForUpdate.getVisibility());
        subProjectForUpdate.setStatus(subProjectDtoForUpdate.getStatus());
        subProjectForUpdate.setUpdatedAt(LocalDateTime.now());
        subProjectForUpdate.getChildren().stream().forEach(child -> child.setStatus(subProjectDtoForUpdate.getStatus()));

        when(projectRepository.getProjectById(anyLong())).thenReturn(subProjectForUpdate);
        when(projectRepository.save(any(Project.class))).thenReturn(subProjectForUpdate);
        when(subProjectMapper.toDto(subProjectForUpdate)).thenReturn(subProjectDtoForUpdate);

        SubProjectDto result = subProjectService.updateSubProject(subProjectDtoForUpdate);

        assertEquals(subProjectDtoForUpdate, result);
    }

    @ParameterizedTest
    @MethodSource("argsProvider1")
    public void testIsApplicable(ProjectFilter projectFilter) {
        ProjectFilterDto filter = new ProjectFilterDto("f", List.of(ProjectStatus.COMPLETED));
        ProjectFilterDto filter2 = new ProjectFilterDto();

        boolean result1 = projectFilter.isApplicable(filter);
        boolean result2 = projectFilter.isApplicable(filter2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @ParameterizedTest
    @MethodSource("argsProvider2")
    public void testApply(ProjectFilter projectFilter, ProjectFilterDto filter) {
        Project project1 = Project.builder().name("Args").status(ProjectStatus.COMPLETED).build();
        Project project2 = Project.builder().name("no").status(ProjectStatus.CANCELLED).build();

        List<Project> result = projectFilter.apply(Stream.of(project1, project2), filter).collect(Collectors.toList());

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(project1, result.get(0))
        );
    }

    private SubProjectDto createSubProjectDto(String name, String description, Long parentId, List<Long> childrenId, ProjectVisibility visibility, ProjectStatus status) {
        return SubProjectDto.builder()
                .name(name)
                .description(description)
                .parentId(parentId)
                .childrenId(childrenId)
                .visibility(visibility)
                .status(status)
                .build();
    }

    private Project createProject(Long id, String name, ProjectVisibility visibility, List<Project> children) {
        return Project.builder()
                .id(id)
                .name(name)
                .visibility(visibility)
                .children(children)
                .build();
    }
}