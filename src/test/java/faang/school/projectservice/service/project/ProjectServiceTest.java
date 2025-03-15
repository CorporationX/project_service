package faang.school.projectservice.service.project;

import faang.school.projectservice.repository.adapter.ProjectRepositoryAdapter;
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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.constant.ProjectTestConstants.OWNER_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.TEST_PROJECT;
import static faang.school.projectservice.constant.ProjectTestConstants.TEST_PROJECT_ID;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Spy
    private ProjectMapperImpl projectMapper;

    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> captor;

    private final ProjectDto generalDto = new ProjectDto();
    private final TeamMember teamMember = new TeamMember();
    private final Team team = new Team();
    private final List<Project> projects = new ArrayList<>();
    private final ProjectFilterDto filter = new ProjectFilterDto();
    private final List<ProjectFilter> projectFilters = new ArrayList<>();

    @BeforeEach
    void init() {
        teamMember.setUserId(OWNER_ID);
        team.setTeamMembers(List.of(teamMember));

        Project repairComputer = Project.builder()
                .name("Computer repair")
                .visibility(ProjectVisibility.PUBLIC)
                .teams(List.of(team))
                .build();

        Project lifeStyleBlog = Project.builder()
                .name("Lifestyle blog")
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .build();

        projects.add(TEST_PROJECT);
        projects.add(repairComputer);
        projects.add(lifeStyleBlog);

        projectFilters.add(new ProjectNameFilter());

        projectService = new ProjectService(projectRepository, projectRepositoryAdapter, projectMapper, projectFilters);
    }

    @Test
    void testRemoveInvalidSymbolFromNameProject() {
        String titleProject = "Project%_= name*;&^@";

        String resultTitle = projectService.nameAdjustment(titleProject);

        Assertions.assertEquals("project name", resultTitle);
    }

    @Test
    void testExistNameProjectByUser() {
        generalDto.setOwnerId(2L);
        generalDto.setName("project name");
        generalDto.setDescription("some description");

        Mockito.when(projectRepository.existsByOwnerIdAndName(Mockito.eq(2L), Mockito.eq("project name"))).thenReturn(true);

        Assertions.assertThrows(DataAlreadyExistException.class, () -> projectService.createProject(generalDto));

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsByOwnerIdAndName(Mockito.eq(2L), Mockito.eq("project name"));
    }

    @Test
    void testSuccessCreateProject() {
        String projectName = "project name";
        String projectDescription = "some description";

        generalDto.setOwnerId(OWNER_ID);
        generalDto.setName(projectName);
        generalDto.setDescription(projectDescription);

        ProjectDto expectedDto = new ProjectDto();
        expectedDto.setOwnerId(OWNER_ID);
        expectedDto.setName(projectName);
        expectedDto.setDescription(projectDescription);
        expectedDto.setStatus(ProjectStatus.CREATED);

        Project entityProject = new Project();
        entityProject.setOwnerId(OWNER_ID);
        entityProject.setName(projectName);
        entityProject.setDescription(projectDescription);

        Mockito.when(projectRepository.existsByOwnerIdAndName(Mockito.eq(OWNER_ID), Mockito.eq(projectName)))
                .thenReturn(false);
        Mockito.when(projectMapper.toEntity(Mockito.any(ProjectDto.class))).thenReturn(entityProject);
        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenReturn(entityProject);
        Mockito.when(projectMapper.toDto(Mockito.any(Project.class))).thenReturn(expectedDto);

        ProjectDto result = projectService.createProject(generalDto);

        Mockito.verify(projectRepository, Mockito.times(1))
                .existsByOwnerIdAndName(Mockito.eq(OWNER_ID), Mockito.eq(projectName));
        Mockito.verify(projectRepository, Mockito.times(1)).save(captor.capture());
        Mockito.verify(projectMapper, Mockito.times(1)).toEntity(Mockito.any(ProjectDto.class));
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(Mockito.any(Project.class));

        Project projectCaptured = captor.getValue();
        Assertions.assertEquals(projectName, projectCaptured.getName());
        Assertions.assertEquals(OWNER_ID, projectCaptured.getOwnerId());
        Assertions.assertEquals(projectDescription, projectCaptured.getDescription());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedDto.getOwnerId(), result.getOwnerId());
        Assertions.assertEquals(expectedDto.getName(), result.getName());
        Assertions.assertEquals(expectedDto.getDescription(), result.getDescription());
        Assertions.assertEquals(expectedDto.getStatus(), result.getStatus());
    }

    @Test
    void testDoesNotFoundProjectForUpdated() {
        generalDto.setId(10L);

        Mockito.when(projectRepository.findById(10L)).thenReturn(Optional.empty());

        Assertions.assertThrows(DataNotFoundException.class, () -> projectService.updateProject(generalDto));
    }

    @Test
    void testUpdatedDescriptionAndStatusProject() {
        ProjectDto expectedDto = ProjectDto.builder()
                .id(1L)
                .description("new description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(TEST_PROJECT));
        Mockito.doNothing().when(projectMapper).update(expectedDto, TEST_PROJECT);
        Mockito.when(projectMapper.toDto(TEST_PROJECT)).thenReturn(expectedDto);

        ProjectDto result = projectService.updateProject(expectedDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedDto.getDescription(), result.getDescription());
        Assertions.assertEquals(expectedDto.getStatus(), result.getStatus());

        Mockito.verify(projectRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(projectMapper, Mockito.times(1)).update(expectedDto, TEST_PROJECT);
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(TEST_PROJECT);
    }

    @Test
    void testGetPublicProjectsWithoutFilters() {
        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        Mockito.when(projectMapper.toDto(Mockito.any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, TEST_PROJECT_ID);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Test", result.get(0).getName());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        Mockito.verify(projectRepository, Mockito.times(1)).findAll();
        Mockito.verify(projectMapper, Mockito.times(2)).toDto(Mockito.any());
    }

    @Test
    void testGetPublicAndPrivateProjectsWithoutFilters() {
        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        Mockito.when(projectMapper.toDto(Mockito.any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, 1L);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("Test", result.get(0).getName());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        Mockito.verify(projectRepository, Mockito.times(1)).findAll();
        Mockito.verify(projectMapper, Mockito.times(3)).toDto(Mockito.any());
    }

    @Test
    void testGetProjectsWithNameFilter() {
        filter.setNamePattern("Test");

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        Mockito.when(projectMapper.toDto(Mockito.any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, TEST_PROJECT_ID);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Test", result.get(0).getName());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        Mockito.verify(projectRepository, Mockito.times(1)).findAll();
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    void testAllProjectByUserId() {
        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        Mockito.when(projectMapper.toDto(Mockito.any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUser(1L);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("Test", result.get(0).getName());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        Mockito.verify(projectRepository, Mockito.times(1)).findAll();
        Mockito.verify(projectMapper, Mockito.times(3)).toDto(Mockito.any());
    }

    @Test
    void testProjectByIdNotFound() {
        Mockito.when(projectRepositoryAdapter.getById(30L)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(30L));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(30L);
    }

    @Test
    void testGetProjectById() {
        generalDto.setId(TEST_PROJECT_ID);
        generalDto.setName("Test");
        generalDto.setDescription("Test project");
        generalDto.setVisibility(ProjectVisibility.PUBLIC);
        generalDto.setStatus(ProjectStatus.CREATED);

        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(TEST_PROJECT);
        Mockito.when(projectMapper.toDto(TEST_PROJECT)).thenReturn(generalDto);

        ProjectDto result = projectService.getProjectById(TEST_PROJECT_ID);

        Assertions.assertEquals("Test", result.getName());
        Assertions.assertEquals("Test project", result.getDescription());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.getVisibility());

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(TEST_PROJECT);
    }
}
