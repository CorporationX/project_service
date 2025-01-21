import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataAlreadyExistException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.filter.ProjectNameFilter;
import faang.school.projectservice.filter.ProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @InjectMocks
    private ProjectService projectService;
    Project projectPublic;
    Project projectPrivate;
    Team teamPublic;
    Team teamPrivate;
    TeamMember teamMemberPublic;
    TeamMember teamMemberPrivate;
    ProjectNameFilter nameFilter;
    ProjectStatusFilter statusFilter;
    List<ProjectFilter> projectFilters;
    List<Project> allProject;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Captor
    private ArgumentCaptor<Project> captor;

//    @BeforeEach
//    public void setUp() {
//        teamMemberPublic.builder()
//                .userId(1L)
//                .build();
//
//        teamPublic.builder()
//                .teamMembers(List.of(teamMemberPublic))
//                .build();
//
//        projectPublic.builder()
//                .teams(List.of(teamPublic))
//                .visibility(ProjectVisibility.PUBLIC)
//                .build();
//
//        teamMemberPrivate.builder()
//                .userId(2L)
//                .build();
//
//        teamPrivate.builder()
//                .teamMembers(List.of(teamMemberPrivate))
//                .build();
//
//        projectPrivate.builder()
//                .teams(List.of(teamPrivate))
//                .visibility(ProjectVisibility.PRIVATE);
//
//        allProject = List.of(projectPublic, projectPrivate);
//    }

    @Test
    public void testCreateProjectWithoutTitle() {
        ProjectDto dto = new ProjectDto();
        dto.setName(" ");

        assertThrows(DataValidateException.class, () -> projectService.createProject(dto));
    }

    @Test
    public void testCreateProjectWithoutDescription() {
        ProjectDto dto = new ProjectDto();
        dto.setName("Project name");
        dto.setDescription(" ");

        assertThrows(DataValidateException.class, () -> projectService.createProject(dto));
    }

    @Test
    public void testRemoveInvalidSymbolFromNameProject() {
        String titleProject = "Project%_= name*;&^@";

        String resultTitle = projectService.nameAdjustment(titleProject);

        assertEquals("project name", resultTitle);
    }

    @Test
    public void testExistNameProjectByUser() {
        ProjectDto dto = new ProjectDto();
        dto.setName("project name");
        dto.setDescription("some description");

        when(projectRepository.existsByOwnerIdAndName(dto.getOwnerId(), dto.getName())).thenReturn(true);

        assertThrows(DataAlreadyExistException.class, () -> projectService.createProject(dto));

        verify(projectRepository, times(1)).existsByOwnerIdAndName(dto.getOwnerId(), dto.getName());
    }

    @Test
    public void testSuccessCreateProject() {
        Long ownerId = 1L;
        String projectName = "project name";
        String projectDescription = "some description";

        ProjectDto dto = new ProjectDto();
        dto.setOwnerId(ownerId);
        dto.setName(projectName);
        dto.setDescription(projectDescription);

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

        ProjectDto result = projectService.createProject(dto);

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
        Long projectId = 1L;
        ProjectDto dto = new ProjectDto();
        dto.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> projectService.updatedProject(dto));
    }

    @Test
    public void testUpdatedIdenticalDescriptionAndStatusProject() {
        Project project = Project.builder()
                .id(1L)
                .description("some description")
                .status(ProjectStatus.CREATED)
                .build();

        ProjectDto expectedDto = ProjectDto.builder()
                .id(1L)
                .description("some description")
                .status(ProjectStatus.CREATED)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        ProjectDto resultDto = projectService.updatedProject(expectedDto);

        assertNotNull(resultDto);
        assertEquals(expectedDto.getDescription(), resultDto.getDescription());
        assertEquals(expectedDto.getStatus(), resultDto.getStatus());

        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(project);
        verify(projectMapper, times(1)).toDto(project);
    }

    @Test
    public void testUpdatedDescriptionAndStatusProject() {
        Project project = Project.builder()
                .id(1L)
                .description("some description")
                .status(ProjectStatus.ON_HOLD)
                .build();

        ProjectDto expectedDto = ProjectDto.builder()
                .id(1L)
                .description("new description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(expectedDto);

        ProjectDto result = projectService.updatedProject(expectedDto);

        assertNotNull(result);
        assertEquals(expectedDto.getDescription(), result.getDescription());
        assertEquals(expectedDto.getStatus(), result.getStatus());

        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(project);
        verify(projectMapper, times(1)).toDto(project);
    }

    @Test
    public void testGetPublicProjectsWithoutFilters() {
        ProjectFilterDto filterDto = new ProjectFilterDto();

        Project publicProject = Project.builder()
                .name("Project public")
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Project privateProject = Project.builder()
                .name("Project private")
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        List<Project> projects = List.of(publicProject, privateProject);

        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDto(any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return new ProjectDto();
        });

        List<ProjectDto> result = projectService.getProjectWithFilters(new ProjectFilterDto(), 100L);

        assertEquals(1, result.size());
    }
}
