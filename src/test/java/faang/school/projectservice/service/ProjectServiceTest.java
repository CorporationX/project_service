package faang.school.projectservice.service;

import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.BadRequestException;
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
import faang.school.projectservice.service.minio.ProjectCoverMinioService;
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

import static faang.school.projectservice.ProjectTestConstants.CHAIR_PROJECT_ID;
import static faang.school.projectservice.ProjectTestConstants.PROJECT_COVER_INPUT_STREAM;
import static faang.school.projectservice.ProjectTestConstants.PROJECT_COVER_MULTIPART_FILE;
import static faang.school.projectservice.ProjectTestConstants.NOT_OWNER_ID;
import static faang.school.projectservice.ProjectTestConstants.OWNER_ID;
import static faang.school.projectservice.ProjectTestConstants.PROJECT_COVER_IMAGE_ID;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Captor
    private ArgumentCaptor<Project> captor;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Mock
    private ProjectCoverMinioService projectCoverMinioService;

    private ProjectService projectService;

    private final ProjectDto generalDto = new ProjectDto();

    private final TeamMember teamMember = new TeamMember();

    private final Team team = new Team();

    private Project chairProject;
    private Project repairComputer;
    private Project lifeStyleBlog;

    private final List<Project> projects = new ArrayList<>();

    private final ProjectFilterDto filter = new ProjectFilterDto();

    private final List<ProjectFilter> projectFilters = new ArrayList<>();

    @BeforeEach
    void init() {
        teamMember.setUserId(OWNER_ID);
        team.setTeamMembers(List.of(teamMember));

        chairProject = Project.builder()
                .id(CHAIR_PROJECT_ID)
                .name("Chairs hand made")
                .description("some description")
                .ownerId(OWNER_ID)
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

        projects.add(chairProject);
        projects.add(repairComputer);
        projects.add(lifeStyleBlog);

        projectFilters.add(new ProjectNameFilter());

        projectService = new ProjectService(projectRepository, projectRepositoryAdapter, projectMapper, projectFilters,
                projectCoverMinioService);
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

        Mockito.when(projectRepository.existsByOwnerIdAndName(Mockito.eq(OWNER_ID), Mockito.eq(projectName))).thenReturn(false);
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

        Assertions.assertThrows(DataNotFoundException.class, () -> projectService.updatedProject(generalDto));
    }

    @Test
    void testUpdatedDescriptionAndStatusProject() {
        ProjectDto expectedDto = ProjectDto.builder()
                .id(1L)
                .description("new description")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(chairProject));
        Mockito.doNothing().when(projectMapper).updateProject(expectedDto, chairProject);
        Mockito.when(projectMapper.toDto(chairProject)).thenReturn(expectedDto);

        ProjectDto result = projectService.updatedProject(expectedDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedDto.getDescription(), result.getDescription());
        Assertions.assertEquals(expectedDto.getStatus(), result.getStatus());

        Mockito.verify(projectRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(projectMapper, Mockito.times(1)).updateProject(expectedDto, chairProject);
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(chairProject);
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

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, 100L);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Chairs hand made", result.get(0).getName());
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
        Assertions.assertEquals("Chairs hand made", result.get(0).getName());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.get(0).getVisibility());

        Mockito.verify(projectRepository, Mockito.times(1)).findAll();
        Mockito.verify(projectMapper, Mockito.times(3)).toDto(Mockito.any());
    }

    @Test
    void testGetProjectsWithNameFilter() {
        filter.setNamePattern("Repair");

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        Mockito.when(projectMapper.toDto(Mockito.any())).thenAnswer(invocationOnMock -> {
            Project currentProject = invocationOnMock.getArgument(0);
            return ProjectDto.builder()
                    .name(currentProject.getName())
                    .visibility(currentProject.getVisibility())
                    .build();
        });

        List<ProjectDto> result = projectService.getAllAvailableProjectsForUserWithFilter(filter, 100L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Repair computers", result.get(0).getName());
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
        Assertions.assertEquals("Chairs hand made", result.get(0).getName());
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
        generalDto.setId(100L);
        generalDto.setName("Chairs hand made");
        generalDto.setDescription("some description");
        generalDto.setVisibility(ProjectVisibility.PUBLIC);
        generalDto.setStatus(ProjectStatus.CREATED);

        Mockito.when(projectRepositoryAdapter.getById(100L)).thenReturn(chairProject);
        Mockito.when(projectMapper.toDto(chairProject)).thenReturn(generalDto);

        ProjectDto result = projectService.getProjectById(100L);

        Assertions.assertEquals("Chairs hand made", result.getName());
        Assertions.assertEquals("some description", result.getDescription());
        Assertions.assertEquals(ProjectVisibility.PUBLIC, result.getVisibility());

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(100L);
        Mockito.verify(projectMapper, Mockito.times(1)).toDto(chairProject);
    }

    @Test
    void addProjectCover_shouldThrowBadRequestException_whenTheUserIsNotTheOwnerOfTheProject() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectService.addProjectCover(CHAIR_PROJECT_ID, PROJECT_COVER_MULTIPART_FILE, NOT_OWNER_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
    }

    @Test
    void addProjectCover_shouldThrowBadRequestException_whenProjectCoverImageIdIsNotNull() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        chairProject.setCoverImageId(PROJECT_COVER_IMAGE_ID);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectService.addProjectCover(CHAIR_PROJECT_ID, PROJECT_COVER_MULTIPART_FILE, OWNER_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
    }

    @Test
    void addProjectCover_shouldBeCompletedSuccessfully() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);
        Mockito.when(projectCoverMinioService.uploadProjectCover(PROJECT_COVER_MULTIPART_FILE))
                .thenReturn(PROJECT_COVER_IMAGE_ID);

        projectService.addProjectCover(CHAIR_PROJECT_ID, PROJECT_COVER_MULTIPART_FILE, OWNER_ID);

        Assertions.assertEquals(PROJECT_COVER_IMAGE_ID, chairProject.getCoverImageId());

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
        Mockito.verify(projectCoverMinioService, Mockito.times(1))
                .uploadProjectCover(PROJECT_COVER_MULTIPART_FILE);
    }

    @Test
    void deleteProjectCover_shouldThrowBadRequestException_whenTheUserIsNotTheOwnerOfTheProject() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectService.deleteProjectCover(CHAIR_PROJECT_ID, NOT_OWNER_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
    }

    @Test
    void deleteProjectCover_shouldThrowBadRequestException_whenProjectCoverImageIdIsNull() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectService.deleteProjectCover(CHAIR_PROJECT_ID, OWNER_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
    }

    @Test
    void deleteProjectCover_shouldBeCompletedSuccessfully() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        chairProject.setCoverImageId(PROJECT_COVER_IMAGE_ID);

        Mockito.when(projectCoverMinioService.removeProjectCover(PROJECT_COVER_IMAGE_ID))
                .thenReturn(PROJECT_COVER_IMAGE_ID);

        projectService.deleteProjectCover(CHAIR_PROJECT_ID, OWNER_ID);

        Assertions.assertNull(chairProject.getCoverImageId());

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
        Mockito.verify(projectCoverMinioService, Mockito.times(1))
                .removeProjectCover(PROJECT_COVER_IMAGE_ID);
    }

    @Test
    void getProjectCover_shouldThrowBadRequestException_whenProjectCoverImageIdIsNull() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        Assertions.assertThrows(BadRequestException.class, () -> projectService.getProjectCover(CHAIR_PROJECT_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
    }

    @Test
    void getProjectCover_shouldBeCompletedSuccessfully() {
        Mockito.when(projectRepositoryAdapter.getById(CHAIR_PROJECT_ID)).thenReturn(chairProject);

        chairProject.setCoverImageId(PROJECT_COVER_IMAGE_ID);

        Mockito.when(projectCoverMinioService.getProjectCover(PROJECT_COVER_IMAGE_ID))
                .thenReturn(PROJECT_COVER_INPUT_STREAM);

        Assertions.assertEquals(PROJECT_COVER_INPUT_STREAM, projectService.getProjectCover(CHAIR_PROJECT_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(CHAIR_PROJECT_ID);
        Mockito.verify(projectCoverMinioService, Mockito.times(1))
                .getProjectCover(PROJECT_COVER_IMAGE_ID);
    }
}
