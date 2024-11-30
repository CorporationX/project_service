package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectCreateResponseDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectUpdateResponseDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.exception.InsufficientStorageException;
import faang.school.projectservice.exception.ProjectVisibilityException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.filter.projectfilter.ProjectStatusFilter;
import faang.school.projectservice.mapper.project.ProjectMapperImpl;
import faang.school.projectservice.mapper.project.UpdateProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.statusupdator.StatusUpdater;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Mock
    private ResourceValidator resourceValidator;

    @Spy
    private ProjectMapperImpl projectMapper;

    @Spy
    private UpdateProjectMapperImpl updateProjectMapper;

    @Mock
    List<Filter<Project, ProjectFilterDto>> filters;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Project> captor;

    private Project project;
    private ProjectDto projectDto;
    private UpdateProjectDto updateProjectDto;
    private UpdateProjectDto emptyUpdateProjectDto;
    private Project mockProject;
    private ProjectFilterDto filterDto;
    private Long projectId;
    private Long ownerId;
    private Project parentProject;
    private CreateProjectDto createProjectDto;
    private Project subProject;
    private UpdateSubProjectDto updateSubProjectDto;
    private List<StatusUpdater> statusUpdates;
    private ProjectFilterDto projectFilterDto;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(null)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        projectDto = ProjectDto.builder()
                .name("Test project")
                .description("Test project description")
                .ownerId(1L)
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();

        updateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description("Updated test project description")
                .ownerId(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        emptyUpdateProjectDto = UpdateProjectDto.builder()
                .name("Test project")
                .description(null)
                .ownerId(1L)
                .status(null)
                .visibility(null)
                .build();

        filterDto = ProjectFilterDto.builder().status(ProjectStatus.IN_PROGRESS).build();
        projectId = project.getId();
        ownerId = 1L;
        mockProject = mock(Project.class);
    }

    @Test
    void testGetByIdThrowsException() {
        when(projectRepository.getProjectById(projectId)).thenThrow(new EntityNotFoundException(
                String.format("Project not found by id: %s", projectId)));

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(projectId),
                String.format("Project not found by id: %s", projectId));
    }

    @Test
    void testGetByIdSuccessfully() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        Project result = projectService.getProjectById(projectId);

        assertEquals(project.getName(), result.getName());
    }

    @Test
    void testCreateProjectSuccessful() {
        when(projectRepository.save(project)).thenReturn(project);
        project.setStatus(ProjectStatus.CREATED);

        projectService.createProject(projectDto);

        verify(projectRepository, times(1)).save(captor.capture());
        Project result = captor.getValue();
        assertEquals(result, project);
    }

    @Test
    void testUpdateProjectShouldNotUpdateIfValuesNull() {
        when(projectRepository.getProjectById(projectId)).thenReturn(mockProject);
        when(projectRepository.save(mockProject)).thenReturn(mockProject);

        projectService.updateProject(emptyUpdateProjectDto);

        verify(mockProject, times(0)).setDescription(updateProjectDto.getDescription());
        verify(mockProject, times(0)).setStatus(updateProjectDto.getStatus());
        verify(mockProject, times(0)).setVisibility(updateProjectDto.getVisibility());
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    @DisplayName("Get project by id success")
    void testGetProjectByIdSuccess() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        Project result = projectService.getProjectById(project.getId());

        assertNotNull(result);
        assertEquals(project, result);
        assertEquals("Test project", result.getName());
    }

    @Test
    @DisplayName("Get project by id fail")
    void testGetProjectByIdFail() {
        project.setId(1L);
        when(projectRepository.getProjectById(project.getId())).
                thenThrow(new EntityNotFoundException(String.format("Project with id %s doesn't exist", project.getId())));

        Exception ex = assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(project.getId()));
        assertEquals("Project with id 1 doesn't exist", ex.getMessage());
    }

    @Test
    void testUpdateProjectSuccessful() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);

        UpdateProjectDto result = projectService.updateProject(updateProjectDto);

        verify(projectRepository, times(1)).save(project);
        assertEquals(result.getDescription(), updateProjectDto.getDescription());
        assertEquals(result.getStatus(), updateProjectDto.getStatus());
        assertEquals(result.getVisibility(), updateProjectDto.getVisibility());
    }

    @Test
    void testGetProjectsByFilterShouldFilterPrivateAndOwn() {
        List<Project> notFilteredProjects = getProjectsList();
        List<ProjectDto> filteredProjectDtos = getProjectDtosList();
        ProjectStatusFilter statusFilter = new ProjectStatusFilter();
        filters = List.of(statusFilter);
        projectValidator = new ProjectValidator(projectRepository);
        projectService = new ProjectService(eventPublisher, projectRepository, projectValidator, resourceValidator,
                projectMapper, updateProjectMapper, filters, statusUpdates);
        when(projectRepository.findAll()).thenReturn(notFilteredProjects);

        List<ProjectDto> result = projectService.getProjectsByFilter(filterDto, ownerId);

        verify(projectRepository, times(1)).findAll();
        assertEquals(filteredProjectDtos, result);
    }

    @Test
    void testGetAllProjectsForUserSuccess() {
        List<Project> allProjects = getProjectsList();
        allProjects.get(2).setVisibility(ProjectVisibility.PRIVATE);
        List<ProjectDto> availableProjectDtos = getProjectDtosList();
        projectValidator = new ProjectValidator(projectRepository);
        projectService = new ProjectService(eventPublisher, projectRepository, projectValidator, resourceValidator,
                projectMapper, updateProjectMapper, filters, statusUpdates);

        when(projectRepository.findAll()).thenReturn(allProjects);

        List<ProjectDto> result = projectService.getAllProjectsForUser(1L);

        assertEquals(availableProjectDtos, result);
    }

    @Test
    void testGetAccessibleProjectByIdSuccess() {
        project.setId(1L);
        project.setStatus(ProjectStatus.CREATED);
        projectDto.setId(1L);
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        when(projectValidator.canUserAccessProject(project, ownerId)).thenReturn(true);

        ProjectDto result = projectService.getAccessibleProjectById(project.getId(), ownerId);

        assertEquals(projectDto, result);
    }

    @Test
    void testGetAccessibleProjectByIdShouldThrowException() {
        project.setId(1L);
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);

        assertThrows(EntityNotFoundException.class, () ->
                projectService.getAccessibleProjectById(project.getId(), ownerId));
    }

    @Test
    void testFindAllByIdWhenProjectsExist() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);
        Project project3 = new Project();
        project3.setId(3L);
        when(projectRepository.findAllByIds(ids)).thenReturn(List.of(project1, project2, project3));

        List<ProjectDto> result = projectService.findAllById(ids);

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    void testFindAllByIdWhenNoProjectsFound() {
        List<Long> ids = List.of(1L, 2L, 3L);
        when(projectRepository.findAllByIds(ids)).thenReturn(List.of());

        List<ProjectDto> result = projectService.findAllById(ids);
        assertEquals(0, result.size());
    }

    private List<Project> getProjectsList() {
        return List.of(
                Project.builder()
                        .ownerId(1L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PUBLIC)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                Project.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PUBLIC)
                        .status(ProjectStatus.COMPLETED)
                        .build(),
                Project.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        );
    }

    private List<ProjectDto> getProjectDtosList() {
        return List.of(
                ProjectDto.builder()
                        .ownerId(1L)
                        .visibility(ProjectVisibility.PRIVATE)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build(),
                ProjectDto.builder()
                        .ownerId(2L)
                        .visibility(ProjectVisibility.PUBLIC)
                        .status(ProjectStatus.IN_PROGRESS)
                        .build()
        );
    }

    @Test
    void findByIdWhenProjectExistsShouldReturnProjectDto() {
        when(projectRepository.getProjectById(1L)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.findById(1L);

        assertNotNull(result);
        assertEquals(projectDto.getId(), result.getId());
        assertEquals(projectDto.getName(), result.getName());

        verify(projectRepository).getProjectById(1L);
        verify(projectMapper).toDto(project);
    }

    @Test
    void findByIdWhenProjectDoesNotExistShouldThrowException() {
        when(projectRepository.getProjectById(1L)).thenThrow(new EntityNotFoundException("Project not found"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            projectService.findById(1L);
        });

        assertEquals("Project not found", exception.getMessage());

        verify(projectRepository).getProjectById(1L);
    }

    @Test
    void findAllById_whenProjectsExist_shouldReturnProjectDtos() {
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllByIds(ids)).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        List<ProjectDto> result = projectService.findAllById(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectDto.getId(), result.get(0).getId());
        assertEquals(projectDto.getName(), result.get(0).getName());

        verify(projectRepository).findAllByIds(ids);
        verify(projectMapper).toDto(project);
    }

    @Test
    void findAllById_whenNoProjectsExist_shouldReturnEmptyList() {
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllByIds(ids)).thenReturn(List.of());

        List<ProjectDto> result = projectService.findAllById(ids);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectRepository).findAllByIds(ids);
    }

    @Test
    @DisplayName("CreateSubProject success")
    void testCreateSubProject() {
        initializeForCreateSubProjectTest();
        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        doNothing().when(projectValidator).validateCreateSubprojectBasedOnVisibility(parentProject, createProjectDto);
        doNothing().when(projectValidator).validateUniqueProject(createProjectDto);
        when(projectMapper.toEntity(createProjectDto)).thenReturn(subProject);
        when(projectRepository.save(parentProject)).thenReturn(parentProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        when(projectMapper.toCreateResponseDto(subProject)).thenReturn(
                ProjectCreateResponseDto.builder()
                        .name(subProject.getName())
                        .build()
        );

        ProjectCreateResponseDto responseDto = projectService.createSubProject(parentProject.getId(), createProjectDto);

        verify(projectRepository).getProjectById(parentProject.getId());
        verify(projectValidator).validateCreateSubprojectBasedOnVisibility(parentProject, createProjectDto);
        verify(projectValidator).validateUniqueProject(createProjectDto);
        verify(projectRepository).save(parentProject);
        verify(projectRepository).save(subProject);

        assertNotNull(responseDto);
        assertEquals(subProject.getName(), responseDto.getName());
    }

    @Test
    @DisplayName("CreateSubProject parent not found")
    void testCreateSubProject_ParentNotFound() {
        initializeForCreateSubProjectTest();
        when(projectRepository.getProjectById(parentProject.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () ->
                projectService.createSubProject(parentProject.getId(), createProjectDto));
    }

    @Test
    @DisplayName("CreateSubProject validation fails")
    void testCreateSubProject_ValidationFails() {
        initializeForCreateSubProjectTest();
        doThrow(new ProjectVisibilityException("Validation failed"))
                .when(projectValidator).validateCreateSubprojectBasedOnVisibility(parentProject, createProjectDto);

        when(projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);

        assertThrows(ProjectVisibilityException.class, () ->
                projectService.createSubProject(parentProject.getId(), createProjectDto));
    }

    @Test
    @DisplayName("UpdateSubProject success")
    void testUpdateSubProject() {
        initializeForUpdateSubProjectTest();
        when(projectRepository.getProjectById(updateSubProjectDto.getId())).thenReturn(subProject);
        when(projectRepository.save(subProject)).thenReturn(subProject);
        ProjectUpdateResponseDto responseDto = projectService.updateSubProject(updateSubProjectDto);

        verify(projectRepository, times(1)).getProjectById(updateSubProjectDto.getId());
        verify(projectRepository, times(1)).save(subProject);
        verify(projectValidator, times(1)).validateHasChildrenProjectsClosed(subProject.getParentProject());

        assertEquals(subProject.getName(), responseDto.getName());
        assertEquals(ProjectVisibility.PRIVATE, responseDto.getVisibility());
    }


    @Test
    @DisplayName("UpdateSubProject project not found")
    void testUpdateSubProject_ProjectNotFound() {
        initializeForUpdateSubProjectTest();
        when(projectRepository.getProjectById(updateSubProjectDto.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> projectService.updateSubProject(updateSubProjectDto));
    }

    @Test
    @DisplayName("Filter SubProjects success")
    void testFilterSubProjects() {
        initializeForFilterSubProjectsTest();
        when (projectRepository.getProjectById(parentProject.getId())).thenReturn(parentProject);
        when (projectValidator.isPublicProject(any(Project.class))).thenReturn(true);

        List<ProjectDto> result = projectService.filterSubProjects(parentProject.getId(), projectFilterDto);

        verify(projectRepository, times(1)).getProjectById(any(Long.class));
        verify(projectValidator, times(2)).isPublicProject(any(Project.class));
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Filter SubProjects project not found")
    void testFilterSubProjects_ProjectNotFound() {
        initializeForFilterSubProjectsTest();
        when (projectRepository.getProjectById(parentProject.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> projectService.filterSubProjects(parentProject.getId(), projectFilterDto));
    }

    private void initializeForCreateSubProjectTest() {
        parentProject = new Project();
        parentProject.setId(1L);
        parentProject.setStatus(ProjectStatus.CREATED);
        parentProject.setChildren(new ArrayList<>());

        createProjectDto = CreateProjectDto.builder()
                .name("Subproject")
                .description("A subproject description")
                .build();

        subProject = new Project();
        subProject.setId(2L);
        subProject.setName(createProjectDto.getName());
        subProject.setDescription(createProjectDto.getDescription());
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setVisibility(ProjectVisibility.PUBLIC);
    }

    private void initializeForUpdateSubProjectTest() {
        updateSubProjectDto = UpdateSubProjectDto.builder()
                .id(2L)
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.COMPLETED)
                .build();
        subProject = Project.builder()
                .id(2L)
                .name("Subproject")
                .description("A subproject description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(parentProject)
                .build();

        parentProject = Project.builder()
                .id(1L)
                .name("Parent project")
                .description("A parent project description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private void initializeForFilterSubProjectsTest() {
        subProject = Project.builder()
                .id(2L)
                .name("Subproject")
                .description("A subproject description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .parentProject(parentProject)
                .build();

        parentProject = Project.builder()
                .id(1L)
                .name("Parent project")
                .description("A parent project description")
                .status(ProjectStatus.CREATED)
                .visibility(ProjectVisibility.PUBLIC)
                .children(new ArrayList<>(List.of(subProject, subProject)))
                .build();

        projectFilterDto = ProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    @DisplayName("Increase storage size after file upload with valid input: success")
    void increaseOccupiedStorageSize_ValidInput_Success() {
        MockMultipartFile file = new MockMultipartFile("file", "content".getBytes());

        project.setStorageSize(BigInteger.valueOf(10L));

        projectService.increaseOccupiedStorageSize(project, file);

        verify(resourceValidator, times(1)).validateResourceNotEmpty(file);

        assertEquals(BigInteger.valueOf(17L), project.getStorageSize());
    }

    @Test
    @DisplayName("Increase storage size with empty file: fail")
    void increaseOccupiedStorageSizeAfterFileUpload_EmptyFile_Fail() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        doThrow(new InsufficientStorageException("Not enough space to store files"))
                .when(resourceValidator).validateResourceNotEmpty(file);

        RuntimeException ex = assertThrows(InsufficientStorageException.class, () ->
                projectService.increaseOccupiedStorageSize(project, file));
        assertEquals("Not enough space to store files", ex.getMessage());

        verify(resourceValidator, times(1)).validateResourceNotEmpty(file);
    }

    @Test
    @DisplayName("Decrease storage size after file deleted with valid input: success")
    void decreaseOccupiedStorageSize_ValidInput_Success() {
        BigInteger fileSize = BigInteger.valueOf(5L);

        project.setStorageSize(BigInteger.valueOf(10L));

        projectService.decreaseOccupiedStorageSize(project, fileSize);

        assertEquals(BigInteger.valueOf(5L), project.getStorageSize());
    }
}