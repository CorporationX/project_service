import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataAlreadyExistException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Captor
    private ArgumentCaptor<Project> captor;

    @Spy
    private ProjectMapperImpl projectMapper;
    private ProjectService projectService;
    private ProjectDto generalDto = new ProjectDto();
    private TeamMember teamMember = new TeamMember();
    private Team team = new Team();
    private Project chairProject = new Project();
    private Project repairComputer = new Project();
    private Project lifeStyleBlog = new Project();
    private List<Project> projects = new ArrayList<>();
    private ProjectFilterDto filter = new ProjectFilterDto();
    private List<ProjectFilter> projectFilters = new ArrayList<>();

    @BeforeEach
    void init() {
        teamMember.setUserId(1L);
        team.setTeamMembers(List.of(teamMember));

        chairProject = Project.builder()
                .id(100L)
                .name("Chairs hand made")
                .description("some description")
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();

        repairComputer = Project.builder()
                .name("Repair computers")
                .visibility(ProjectVisibility.PUBLIC)
                .teams(List.of(team))
                .build();

        lifeStyleBlog = Project.builder()
                .name("Blog lifestyle")
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .build();

        projects = List.of(chairProject, repairComputer, lifeStyleBlog);

        projectFilters.add(new ProjectNameFilter());
        projectService = new ProjectService(projectRepository, projectMapper, projectFilters);
    }

    @Test
    public void testRemoveInvalidSymbolFromNameProject() {
        String titleProject = "Project%_= name*;&^@";

        String resultTitle = projectService.nameAdjustment(titleProject);

        assertEquals("project name", resultTitle);
    }

    @Test
    public void testExistNameProjectByUser() {
        generalDto.setOwnerId(2L);
        generalDto.setName("project name");
        generalDto.setDescription("some description");

        when(projectRepository.existsByOwnerIdAndName(eq(2L), eq("project name"))).thenReturn(true);

        assertThrows(DataAlreadyExistException.class, () -> projectService.createProject(generalDto));

        verify(projectRepository, times(1))
                .existsByOwnerIdAndName(eq(2L), eq("project name"));
    }

    @Test
    public void testSuccessCreateProject() {
        Long ownerId = 1L;
        String projectName = "project name";
        String projectDescription = "some description";

        generalDto.setOwnerId(ownerId);
        generalDto.setName(projectName);
        generalDto.setDescription(projectDescription);

        ProjectDto expectedDto = new ProjectDto();
        expectedDto.setOwnerId(ownerId);
        expectedDto.setName(projectName);
        expectedDto.setDescription(projectDescription);
        expectedDto.setStatus(ProjectStatus.CREATED);

        Project entityProject = new Project();
        entityProject.setOwnerId(ownerId);
        entityProject.setName(projectName);
        entityProject.setDescription(projectDescription);

        when(projectRepository.existsByOwnerIdAndName(eq(ownerId), eq(projectName))).thenReturn(false);
        when(projectMapper.toEntity(any(ProjectDto.class))).thenReturn(entityProject);
        when(projectRepository.save(any(Project.class))).thenReturn(entityProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(expectedDto);

        ProjectDto result = projectService.createProject(generalDto);

        verify(projectRepository, times(1)).existsByOwnerIdAndName(eq(ownerId), eq(projectName));
        verify(projectRepository, times(1)).save(captor.capture());
        verify(projectMapper, times(1)).toEntity(any(ProjectDto.class));
        verify(projectMapper, times(1)).toDto(any(Project.class));

        Project projectCaptured = captor.getValue();
        assertEquals(projectName, projectCaptured.getName());
        assertEquals(ownerId, projectCaptured.getOwnerId());
        assertEquals(projectDescription, projectCaptured.getDescription());

        assertNotNull(result);
        assertEquals(expectedDto.getOwnerId(), result.getOwnerId());
        assertEquals(expectedDto.getName(), result.getName());
        assertEquals(expectedDto.getDescription(), result.getDescription());
        assertEquals(expectedDto.getStatus(), result.getStatus());
    }

    @Test
    public void testDoesNotFoundProjectForUpdated() {
        generalDto.setId(10L);

        when(projectRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> projectService.updatedProject(generalDto));
    }

    @Test
    public void testUpdatedDescriptionAndStatusProject() {
        ProjectDto expectedDto = ProjectDto.builder()
                .id(1L)
                .description("new description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(chairProject));
        doNothing().when(projectMapper).updateProject(expectedDto, chairProject);
        when(projectMapper.toDto(chairProject)).thenReturn(expectedDto);

        ProjectDto result = projectService.updatedProject(expectedDto);

        assertNotNull(result);
        assertEquals(expectedDto.getDescription(), result.getDescription());
        assertEquals(expectedDto.getStatus(), result.getStatus());

        verify(projectRepository, times(1)).findById(1L);
        verify(projectMapper, times(1)).updateProject(expectedDto, chairProject);
        verify(projectMapper, times(1)).toDto(chairProject);
    }

    @Test
    public void testGetPublicProjectsWithoutFilters() {
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, 100L);

        assertEquals(2, result.size());
        assertEquals("Chairs hand made", result.get(0).getName());
        assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(2)).toDto(any());
    }

    @Test
    public void testGetPublicAndPrivateProjectsWithoutFilters() {
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, 1L);

        assertEquals(3, result.size());
        assertEquals("Chairs hand made", result.get(0).getName());
        assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(3)).toDto(any());
    }

    @Test
    public void testGetProjectsWithNameFilter() {
        filter.setNamePattern("Repair");

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, 100L);

        assertEquals(1, result.size());
        assertEquals("Repair computers", result.get(0).getName());
        assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(1)).toDto(any());
    }

    @Test
    public void testAllProjectByUserId() {
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUser(1L);

        assertEquals(3, result.size());
        assertEquals("Chairs hand made", result.get(0).getName());
        assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        verify(projectRepository, times(1)).findAll();
        verify(projectMapper, times(3)).toDto(any());
    }

    @Test
    public void testProjectByIdNotFound() {
        when(projectRepository.findById(30L)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> projectService.getProjectById(30L));

        verify(projectRepository, times(1)).findById(30L);
    }

    @Test
    public void testGetProjectById() {
        generalDto.setId(100L);
        generalDto.setName("Chairs hand made");
        generalDto.setDescription("some description");
        generalDto.setVisibility(ProjectVisibility.PUBLIC);
        generalDto.setStatus(ProjectStatus.CREATED);

        when(projectRepository.findById(100L)).thenReturn(Optional.of(chairProject));
        when(projectMapper.toDto(chairProject)).thenReturn(generalDto);

        ProjectDto result = projectService.getProjectById(100L);

        assertEquals("Chairs hand made", result.getName());
        assertEquals("some description", result.getDescription());
        assertEquals(ProjectVisibility.PUBLIC, result.getVisibility());

        verify(projectRepository, times(1)).findById(100L);
        verify(projectMapper, times(1)).toDto(chairProject);
    }
}
