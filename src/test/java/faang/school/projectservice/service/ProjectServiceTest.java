package faang.school.projectservice.service;

import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.FileSizeExceededException;
import faang.school.projectservice.filter.subproject.SubProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.ProjectMomentMapper;
import faang.school.projectservice.mapper.ProjectMomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.utils.image.ImageUtils;
import faang.school.projectservice.validator.FileValidator;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private ProjectMapper projectMapper;
    private ProjectMomentMapper projectMomentMapper;
    private ProjectValidator projectValidator;
    private SubProjectFilter filter;
    private List<SubProjectFilter> filters;

    private StageService stageService;
    private MomentService momentService;

    private FileValidator fileValidator;
    private S3Service s3Service;
    private ImageUtils imageUtils;

    long projectId;
    long ownerId;
    private String projectName;
    private ProjectVisibility visibility;
    private CreateSubProjectDto createSubProjectDto;

    @BeforeEach
    void setUp() {
        projectId = 5L;
        projectName = "Some name";
        ownerId = 10L;
        visibility = ProjectVisibility.PUBLIC;
        createSubProjectDto = CreateSubProjectDto.builder()
                .id(projectId)
                .ownerId(ownerId)
                .visibility(visibility)
                .build();

        projectRepository = Mockito.mock(ProjectRepository.class);
        projectMapper = Mockito.mock(ProjectMapperImpl.class);
        projectMomentMapper = Mockito.mock(ProjectMomentMapperImpl.class);
        projectValidator = Mockito.mock(ProjectValidator.class);
        stageService = Mockito.mock(StageService.class);
        momentService = Mockito.mock(MomentService.class);
        fileValidator = Mockito.mock(FileValidator.class);
        s3Service = Mockito.mock(S3Service.class);
        imageUtils = Mockito.mock(ImageUtils.class);

        filter = Mockito.mock(SubProjectFilter.class);
        filters = List.of(filter);

        projectService = new ProjectService(
                projectRepository,
                projectMapper,
                projectMomentMapper,
                projectValidator,
                filters,
                stageService,
                momentService,
                fileValidator,
                s3Service,
                imageUtils
        );
    }

    @Test
    public void testCreateSubProject() {
        // arrange
        Project project = Project.builder()
                .name(projectName)
                .ownerId(ownerId)
                .visibility(visibility)
                .build();
        Project parentProject = Project.builder()
                .id(projectId)
                .build();

        when(projectMapper.toEntity(createSubProjectDto)).thenReturn(project);
        when(projectRepository.getProjectById(projectId)).thenReturn(parentProject);

        // act
        projectService.createSubProject(projectId, createSubProjectDto);
        // assert
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    public void testCreateSubProjectParentProjectNotFound() {
        // arrange
        when(projectRepository.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> projectService.createSubProject(projectId, any(CreateSubProjectDto.class)));
    }

    @Test
    public void testCreateSubProjectDoesNotPassValidation() {
        // arrange
        Project parentProject = Project.builder()
                .id(projectId)
                .visibility(ProjectVisibility.PRIVATE)
                .status(ProjectStatus.ON_HOLD)
                .build();

        when(projectRepository.getProjectById(projectId))
                .thenReturn(parentProject);
        doThrow(new DataValidationException())
                .when(projectValidator)
                .validateCreateSubProject(parentProject, createSubProjectDto);

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectService.createSubProject(projectId, createSubProjectDto));
    }

    @Test
    public void testUpdateSubProject() {
        // arrange
        Project project = createProjectWithChildren(ProjectVisibility.PUBLIC, ProjectStatus.IN_PROGRESS);

        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        doAnswer(invocation -> {
            project.setVisibility(ProjectVisibility.PRIVATE);
            return null;
        }).when(projectMapper).update(updateDto, project);

        // act
        projectService.updateSubProject(projectId, updateDto);

        // assert
        verify(projectMapper).toProjectDto(project);
    }

    @Test
    public void testUpdateSubProjectCreatesMomentOnCompletion() {
        // arrange
        Project project = createProjectWithChildren(ProjectVisibility.PUBLIC, ProjectStatus.COMPLETED);

        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        Moment moment = Moment.builder().build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(projectMomentMapper.toMoment(project)).thenReturn(moment);
        doAnswer(invocation -> {
            project.setStatus(ProjectStatus.COMPLETED);
            return null;
        }).when(projectMapper).update(updateDto, project);

        // act
        projectService.updateSubProject(projectId, updateDto);

        // assert
        verify(momentService).createMoment(moment);
    }

    @Test
    public void testUpdateSubProjectNotFound() {
        // arrange
        when(projectRepository.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> projectService.updateSubProject(projectId, any(UpdateSubProjectDto.class)));
    }

    @Test
    public void testUpdateSubProjectDoesNotPassValidation() {
        // arrange
        Project project = Project.builder()
                .name(projectName)
                .ownerId(ownerId)
                .visibility(visibility)
                .build();

        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .visibility(ProjectVisibility.PRIVATE)
                .build();

        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);
        doThrow(new DataValidationException())
                .when(projectValidator)
                .validateUpdateSubProject(project, updateDto);

        // act and assert
        assertThrows(DataValidationException.class,
                () -> projectService.updateSubProject(projectId, updateDto));
    }

    @Test
    public void testGetFilteredSubProjects() {
        // arrange
        Project project = createProjectWithChildren(ProjectVisibility.PUBLIC, ProjectStatus.IN_PROGRESS);

        SubProjectFilterDto filterDto = SubProjectFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(filter.isApplicable(filterDto)).thenReturn(true);

        // act
        projectService.getFilteredSubProjects(projectId, filterDto);

        // assert
        verify(projectMapper).toProjectDto(Mockito.anyList());
    }

    @Test
    public void testGetFilteredSubProjectsNullChildren() {
        // arrange
        Project parentProject = Project.builder().build();
        List<ProjectDto> expected = new ArrayList<>();

        when(projectRepository.getProjectById(projectId))
                .thenReturn(parentProject);

        // act
        List<ProjectDto> actual = projectService
                .getFilteredSubProjects(projectId, any(SubProjectFilterDto.class));

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetFilteredSubProjectsParentProjectNotFound() {
        // arrange
        when(projectRepository.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException());

        // act and assert
        assertThrows(EntityNotFoundException.class,
                () -> projectService.getFilteredSubProjects(projectId, any(SubProjectFilterDto.class)));
    }

    @Test
    public void testAddNewCover() {
        // arrange
        MockMultipartFile file = getMultiPartFile();
        InputStream inputStream = new ByteArrayInputStream("some text".getBytes());
        String coverImageId = "imageId";
        Project project = new Project();

        when(imageUtils.getResizedBufferedImage(eq(file), anyInt(), anyInt()))
                .thenReturn(mock(BufferedImage.class));
        when(imageUtils.getBufferedImageInputStream(eq(file), any(BufferedImage.class)))
                .thenReturn(inputStream);

        when(s3Service.uploadFile(
                eq(file),
                eq(inputStream),
                anyString()))
                .thenReturn(coverImageId);

        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        // act
        projectService.addCover(projectId, file);

        // assert
        assertEquals(coverImageId, project.getCoverImageId());
    }

    @Test
    public void testAddNewCoverDeletesOldCoverImage() {
        // arrange
        MockMultipartFile file = getMultiPartFile();
        String oldCoverImageId = "oldCoverImageId";
        Project project = new Project();
        project.setCoverImageId(oldCoverImageId);

        when(projectRepository.getProjectById(projectId))
                .thenReturn(project);

        // act
        projectService.addCover(projectId, file);

        // assert
        verify(s3Service).deleteFile(oldCoverImageId);
    }

    @Test
    public void testAddNewCoverFailsValidation() {
        // arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                new byte[5000]
        );

        doThrow(FileSizeExceededException.class)
                .when(fileValidator).validateFileIsImage(file);

        // act and assert
        assertThrows(FileSizeExceededException.class,
                () -> projectService.addCover(projectId, file));
    }

    @Test
    public void testFindProjectById() {
        // Arrange
        Project project = new Project();
        project.setId(projectId);
        project.setName("Test Project");
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        // Act
        Project result = projectService.findProjectById(projectId);

        // Assert
        assertEquals(project, result);
    }

    @Test
    public void testGetProjectById() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        when(projectRepository.getProjectById(1L)).thenReturn(project);

        // Act
        Project result = projectService.findProjectById(1L);

        // Assert
        assertEquals(project, result);
    }

    private Project createProjectWithChildren(ProjectVisibility visibility, ProjectStatus status) {
        List<Project> children = getListOfProjects(visibility, status);

        return Project.builder()
                .visibility(visibility)
                .status(status)
                .children(children)
                .id(projectId)
                .build();
    }

    private List<Project> getListOfProjects(ProjectVisibility visibility, ProjectStatus status) {
        Project firstChild = Project.builder()
                .visibility(visibility)
                .status(status)
                .build();
        Project secondChild = Project.builder()
                .visibility(visibility)
                .status(status)
                .build();
        Project thirdChild = Project.builder()
                .visibility(visibility)
                .status(status)
                .build();
        return List.of(firstChild, secondChild, thirdChild);
    }

    private MockMultipartFile getMultiPartFile() {
        return new MockMultipartFile(
                "file",
                "file.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[5000]
        );
    }
}