package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.SubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.CoverImageException;
import faang.school.projectservice.exception.DataAlreadyExistingException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.PrivateAccessException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.ProjectFilter;
import faang.school.projectservice.service.filters.ProjectFilterByName;
import faang.school.projectservice.service.filters.ProjectFilterByStatus;
import faang.school.projectservice.util.MultipartFileHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MultipartFileHandler multipartFileHandler;
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private MultipartFile multipartFile;
    @Spy
    private SubProjectMapper subProjectMapper = new SubProjectMapperImpl();
    @Spy
    private ProjectMapper mockProjectMapper = new ProjectMapperImpl();
    @InjectMocks
    private ProjectService projectService;
    ProjectJpaRepository projectJpaRepository;
    private ProjectService testProjectService;
    private SubProjectDto subProjectDto;
    private UpdateSubProjectDto updateSubProjectDto;
    private Project subProject;
    private Project onlyWithIdProject;
    private TeamMember teamMemberCurrentUser;
    private Team teamWithCurrentUser;
    private Team team;
    private TeamMember teamMember;
    ProjectDto projectDto;
    Project project;
    Project project1;
    Project project2;
    Project project3;

    private long currentTime;
    private String originalFileName;
    private String key;
    private String generatedMinioUrl;
    private String bucketName;

    @BeforeEach
    void setUp() {
        List<ProjectFilter> projectFilters = new ArrayList<>(List.of(new ProjectFilterByName(), new ProjectFilterByStatus()));
        projectJpaRepository = Mockito.mock(ProjectJpaRepository.class);
        ProjectRepository testProjectRepository = new ProjectRepository(projectJpaRepository);
        testProjectService = new ProjectService(testProjectRepository, mockProjectMapper, subProjectMapper, projectFilters, multipartFileHandler, amazonS3Service);
        teamMember = TeamMember.builder()
                .userId(2L)
                .build();
        team = Team.builder()
                .teamMembers(List.of(teamMember))
                .build();
        teamMemberCurrentUser = TeamMember.builder()
                .userId(1L)
                .build();
        teamWithCurrentUser = Team.builder()
                .teamMembers(List.of(teamMemberCurrentUser))
                .build();
        projectDto = ProjectDto.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .build();
        LocalDateTime now = LocalDateTime.now();
        project = Project.builder()
                .id(1L)
                .name("Project")
                .description("new Project")
                .ownerId(1L)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(teamWithCurrentUser))
                .status(ProjectStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();
        project1 = Project.builder()
                .id(2L)
                .name("Project1")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        project2 = Project.builder()
                .id(3L)
                .name("Project2")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .teams(List.of(team))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        project3 = Project.builder()
                .id(4L)
                .name("Project3")
                .description("new Project")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PRIVATE)
                .teams(List.of(team))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        this.subProjectDto = SubProjectDto.builder()
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        updateSubProjectDto = UpdateSubProjectDto.builder()
                .id(3L)
                .name("Faang")
                .description("This is Faang")
                .ownerId(1)
                .parentProjectId(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();
        this.subProject = Project.builder()
                .id(2L)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>())
                .build();
        this.onlyWithIdProject = Project.builder()
                .id(1L)
                .build();
        originalFileName = "GoogleImage";
        bucketName = "corpbucket";
        currentTime = System.currentTimeMillis();
        key = project.getId() + project.getName() + currentTime + originalFileName;
        generatedMinioUrl = "http://localhost:9000/" + bucketName + "/" + originalFileName;
    }

    @Test
    void testCreateProject() {
        projectDto.setName("Project^%$^*^^£     C++, Python/C# Мой проект.    ");
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectStatus.CREATED, projectService.create(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals("project c++, python/c# мой проект.", projectService.create(projectDto).getName());
    }

    @Test
    void testSetDefaultVisibilityProject() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectVisibility.PUBLIC, projectService.create(projectDto).getVisibility());
    }

    @Test
    void testSetPrivateVisibilityProject() {
        projectDto.setVisibility(ProjectVisibility.PRIVATE);
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(false);
        Assertions.assertEquals(ProjectVisibility.PRIVATE, projectService.create(projectDto).getVisibility());
    }

    @Test
    void testCreateProjectThrowsException() {
        Mockito.when(projectRepository
                .existsByOwnerUserIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(true);
        DataAlreadyExistingException dataAlreadyExistingException = Assertions
                .assertThrows(DataAlreadyExistingException.class, () -> projectService.create(projectDto));
        Assertions.assertEquals(String.format("User with id: %d already exist project %s",
                projectDto.getOwnerId(), projectDto.getName()), dataAlreadyExistingException.getMessage());
    }

    @Test
    void testUpdateStatus() {
        long projectId = projectDto.getId();
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto).getStatus());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals(project.getDescription(), projectService.update(projectDto).getDescription());
    }

    @Test
    void testUpdateThrowsDataNotExistingException() {
        long projectId = projectDto.getId();
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(null);
        Assertions.assertThrows(DataNotFoundException.class, () -> projectService.update(projectDto));
    }

    @Test
    void testUpdateDescription() {
        long projectId = projectDto.getId();
        projectDto.setDescription("New Description");

        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Assertions.assertEquals("New Description", projectService.update(projectDto).getDescription());
        Mockito.verify(projectRepository).save(any());
        Assertions.assertEquals(project.getStatus(), projectService.update(projectDto).getStatus());
    }

    @Test
    void testUpdateStatusAndDescription() {
        long projectId = projectDto.getId();
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        projectDto.setDescription("New Description");

        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Assertions.assertEquals(ProjectStatus.IN_PROGRESS, projectService.update(projectDto).getStatus());

        Mockito.verify(projectRepository).save(any());
        String descriptionResult = projectService.update(projectDto).getDescription();
        Assertions.assertEquals("New Description", descriptionResult);
    }

    @Test
    void testGetProjectsWithFilter() {
        List<Project> projects = List.of(project, project1, project2, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectFilter> filters = List.of(new ProjectFilterByName(), new ProjectFilterByStatus());
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .projectNamePattern("Proj")
                .status(ProjectStatus.CREATED)
                .build();
        projectService = new ProjectService(projectRepository, mockProjectMapper, subProjectMapper, filters, multipartFileHandler, amazonS3Service);
        List<ProjectDto> filteredProjectsResult =
                List.of(mockProjectMapper.toDto(project2), mockProjectMapper.toDto(project));

        List<ProjectDto> projectsWithFilter = projectService.getProjectsWithFilter(projectFilterDto, 1L);
        Assertions.assertEquals(filteredProjectsResult, projectsWithFilter);
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = List.of(project, project1, project2, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectDto> projectsExpected =
                List.of(mockProjectMapper.toDto(project2), mockProjectMapper.toDto(project));

        List<ProjectDto> projectsResult = projectService.getAllProjects(1L);
        Assertions.assertEquals(projectsExpected, projectsResult);
    }

    @Test
    void testGetEmptyListProjects() {
        List<Project> projects = List.of(project1, project3);

        Mockito.when(projectRepository.findAll()).thenReturn(projects);
        List<ProjectDto> projectsExpected = new ArrayList<>();

        List<ProjectDto> projectsResult = projectService.getAllProjects(1L);
        Assertions.assertEquals(projectsExpected, projectsResult);
    }

    @Test
    void getProjectById() {
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project);
        Assertions.assertEquals(mockProjectMapper.toDto(project),
                projectService.getProjectById(1L, 1L));
    }

    @Test
    void getProjectByIdThrowsPrivateAccessException() {
        Mockito.when(projectRepository.getProjectById(Mockito.anyLong())).thenReturn(project3);
        Assertions.assertThrows(PrivateAccessException.class,
                () -> projectService.getProjectById(1L, 1L));
    }

    @Test
    void createProjectThrowExceptionWhenNameNullOrBlank() {
        ProjectDto failedProjectDto = ProjectDto.builder().build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.create(failedProjectDto));

        assertEquals("Project can't be created with empty name", exception.getMessage());
    }

    @Test
    void createProjectThrowExceptionWhenDescriptionNullOrBlank() {
        ProjectDto failedProjectDto = ProjectDto.builder()
                .name("Hello")
                .description("  ")
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.create(failedProjectDto));

        assertEquals("Project can't be created with empty description", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenProjectIsAlreadyExist() {
        when(projectRepository.existsByOwnerUserIdAndName(subProjectDto.getOwnerId(), subProjectDto.getName()))
                .thenReturn(true);

        DataAlreadyExistingException exception = assertThrows(DataAlreadyExistingException.class, () -> projectService.createSubProject(subProjectDto));

        assertEquals("Project Faang is already exist", exception.getMessage());
    }

    @Test
    void createSubProjectThrowEntityNotFoundException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> testProjectService.createSubProject(subProjectDto));

        assertEquals("Project not found by id: 2", exception.getMessage());
    }

    @Test
    void createSubProjectThrowExceptionWhenTryingToCreatePublicSubProjectOnAPrivateProject() {
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);

        when(projectRepository.existsByOwnerUserIdAndName(1L, "Faang"))
                .thenReturn(false);
        when(projectRepository.getProjectById(2L))
                .thenReturn(Project.builder()
                        .id(10L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .build());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.createSubProject(subProjectDto));

        assertEquals("Public SubProject: Faang, cant be with a private parent Project with id: 10", exception.getMessage());
    }

    @Test
    void createSubProjectTest() {
        when(projectRepository.existsByOwnerUserIdAndName(subProjectDto.getOwnerId(), subProjectDto.getName()))
                .thenReturn(false);
        when(projectRepository.getProjectById(subProjectDto.getParentProjectId()))
                .thenReturn(subProject);

        SubProjectDto result = projectService.createSubProject(subProjectDto);

        assertEquals(subProjectDto, result);
    }

    @Test
    void createSubProjectInvokesGetProjectById() {
        when(projectRepository.existsByOwnerUserIdAndName(subProjectDto.getOwnerId(), subProjectDto.getName()))
                .thenReturn(false);
        when(projectRepository.getProjectById(subProjectDto.getParentProjectId()))
                .thenReturn(subProject);

        projectService.createSubProject(subProjectDto);

        verify(projectRepository, times(2)).getProjectById(subProjectDto.getParentProjectId());
    }

    @Test
    void updateSubProjectThrowEntityNotFoundException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> testProjectService.updateSubProject(updateSubProjectDto));

        assertEquals("Project not found by id: 3", exception.getMessage());
    }

    @Test
    void updateSubProjectThrowExceptionWhenChildrenStatusesAreNotComplete() {
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        Project first = Project.builder()
                .status(ProjectStatus.COMPLETED)
                .build();
        Project second = Project.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .build();
        Project returnProject = Project.builder()
                .children(List.of(first, second))
                .build();

        when(projectRepository.getProjectById(updateSubProjectDto.getId()))
                .thenReturn(returnProject);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> projectService.updateSubProject(updateSubProjectDto));

        assertEquals("Can't close project if subProject status are not complete or cancelled", exception.getMessage());
    }

    @Test
    void updateSubProjectClosingProjectScenarioTest() {
        updateSubProjectDto.setStatus(ProjectStatus.COMPLETED);
        Project first = Project.builder()
                .id(5L)
                .status(ProjectStatus.COMPLETED)
                .build();
        Project second = Project.builder()
                .id(10L)
                .status(ProjectStatus.CANCELLED)
                .build();

        Project returnProject = Project.builder()
                .children(List.of(first, second))
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.getProjectById(Mockito.anyLong()))
                .thenReturn(returnProject);

        LocalDateTime expected = returnProject.getUpdatedAt();

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        assertTrue(expected.isEqual(result));

    }

    @Test
    void updateSubProjectWithPublicVisibilityScenarioTest() {
        Project returnProject = Project.builder()
                .id(5L)
                .name("Faang")
                .visibility(ProjectVisibility.PUBLIC)
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.getProjectById(Mockito.anyLong()))
                .thenReturn(returnProject);

        LocalDateTime expected = returnProject.getUpdatedAt();

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        assertTrue(expected.isEqual(result));
    }

    @Test
    void updateSubProjectWithPublicVisibilityAndNullParentProjectIdScenarioTest() {
        updateSubProjectDto.setParentProjectId(null);
        Project returnProject = Project.builder()
                .id(5L)
                .name("Faang")
                .parentProject(Project.builder()
                        .id(15L)
                        .build())
                .visibility(ProjectVisibility.PUBLIC)
                .updatedAt(LocalDateTime.now())
                .build();

        when(projectRepository.getProjectById(Mockito.anyLong()))
                .thenReturn(returnProject);

        LocalDateTime expected = returnProject.getUpdatedAt();

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        assertTrue(expected.isEqual(result));
    }

    @Test
    void updateSubProjectWithPrivateVisibilityScenarioTest() {
        updateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);

        Project firstProjectChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project firstChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(firstProjectChildren)))
                .build();

        Project secondFirstProjectChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        Project secondSecondProjectFirstChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        Project secondSecondProjectChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(secondSecondProjectFirstChildren)))
                .build();

        Project secondChildren = Project.builder()
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(secondFirstProjectChildren, secondSecondProjectChildren)))
                .build();

        Project returnProject = Project.builder()
                .name("Faang")
                .updatedAt(LocalDateTime.now())
                .children(new ArrayList<>(List.of(firstChildren, secondChildren)))
                .build();

        when(projectRepository.getProjectById(Mockito.anyLong()))
                .thenReturn(returnProject);

        LocalDateTime result = projectService.updateSubProject(updateSubProjectDto);

        ProjectVisibility firstChildrenVisibility = returnProject.getChildren().get(0).getVisibility();
        ProjectVisibility secondChildrenVisibility = returnProject.getChildren().get(1).getVisibility();

        assertTrue(returnProject.getUpdatedAt().isEqual(result));

        assertSame(returnProject.getVisibility(), ProjectVisibility.PRIVATE);
        assertSame(firstChildrenVisibility, ProjectVisibility.PRIVATE);
        assertSame(secondChildrenVisibility, ProjectVisibility.PRIVATE);
    }

    @Test
    void getProjectChildrenWithFilterTest() {
        ProjectFilterDto projectFilterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.IN_PROGRESS)
                .projectNamePattern("name")
                .build();
        Project first = Project.builder()
                .id(1L)
                .name("My first name")
                .description("Some description")
                .ownerId(10L)
                .parentProject(onlyWithIdProject)
                .children(List.of(onlyWithIdProject))
                .status(ProjectStatus.COMPLETED)
                .visibility(ProjectVisibility.PRIVATE)
                .build();
        Project second = Project.builder()
                .id(2L)
                .name("My second name")
                .description("Some description")
                .ownerId(20L)
                .parentProject(onlyWithIdProject)
                .children(List.of(onlyWithIdProject))
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
        List<Project> childrenList = new ArrayList<>(List.of(first, second));

        Project mainProject = Project.builder()
                .children(childrenList)
                .build();

        SubProjectDto secondDto = subProjectMapper.toDto(second);

        List<SubProjectDto> expected = new ArrayList<>(List.of(secondDto));

        Mockito.when(projectJpaRepository.findById(10L))
                .thenReturn(Optional.of(mainProject));

        List<SubProjectDto> result = testProjectService.getProjectChildrenWithFilter(projectFilterDto, 10L);

        assertEquals(expected, result);
        assertEquals(1, result.size());
    }

    @Test
    void addCoverImageTest() {
        byte[] processedImage = new byte[]{0, 1};
        String folder = project.getId() + project.getName();

        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(multipartFileHandler.processCoverImage(multipartFile)).thenReturn(processedImage);
        when(amazonS3Service.uploadFile(processedImage, multipartFile, folder)).thenReturn(key);
        when(multipartFileHandler.generateCoverImageUrl(key)).thenReturn(generatedMinioUrl);

        String result = projectService.addCoverImage(1, multipartFile);

        assertEquals(generatedMinioUrl, result);
        assertEquals(project.getCoverImageId(), key);

        verify(projectRepository).getProjectById(1L);
        verify(multipartFileHandler).processCoverImage(multipartFile);
        verify(amazonS3Service).uploadFile(processedImage, multipartFile, folder);
        verify(multipartFileHandler).generateCoverImageUrl(key);
    }

    @Test
    void getCoverImageByTest() {
        project.setCoverImageId(key);

        when(projectRepository.getProjectById(2L)).thenReturn(project);
        when(multipartFileHandler.generateCoverImageUrl(key)).thenReturn(generatedMinioUrl);

        String result = projectService.getCoverImageBy(2);

        assertEquals(generatedMinioUrl, result);

        verify(projectRepository).getProjectById(2L);
        verify(multipartFileHandler).generateCoverImageUrl(key);
    }

    @Test
    void getCoverImageThrowExceptionTest() {
        when(projectRepository.getProjectById(2L)).thenReturn(project);

        String message = "There is no cover image in project with ID: 2";

        CoverImageException exception = assertThrows(CoverImageException.class, () -> projectService.getCoverImageBy(2));

        assertEquals(message, exception.getMessage());
    }

    @Test
    void deleteCoverImageByTest() {
        project.setCoverImageId(key);

        when(projectRepository.getProjectById(3L)).thenReturn(project);

        projectService.deleteCoverImageBy(3);

        verify(projectRepository).getProjectById(3L);
        verify(amazonS3Service).deleteFile(key);
        verify(projectRepository).deleteCoverImage(3);
    }

    @Test
    void deleteCoverImageByThrowExceptionTest() {
        when(projectRepository.getProjectById(3L)).thenReturn(project);

        String message = "Cover image in project with ID: 3, already deleted";

        CoverImageException exception = assertThrows(CoverImageException.class, () -> projectService.deleteCoverImageBy(3));

        assertEquals(message, exception.getMessage());
    }
}