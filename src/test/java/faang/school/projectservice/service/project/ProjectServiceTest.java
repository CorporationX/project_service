package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.filter.subProjectFilter.SubProjectFilter;
import faang.school.projectservice.filter.subProjectFilter.SubProjectNameFilter;
import faang.school.projectservice.filter.subProjectFilter.SubProjectStatusFilter;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.service.amazonS3.S3Service;
import faang.school.projectservice.service.project.pic.ImageSize;
import faang.school.projectservice.service.project.pic.PicProcessor;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static faang.school.projectservice.model.ProjectStatus.*;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    private ProjectRepository projectRepository;
    private ProjectMapperImpl projectMapper;
    private MomentService momentService;
    private List<SubProjectFilter> filters;
    private SubProjectValidator validator;
    private ProjectValidator projectValidator;
    private Project mockProject;

    private ProjectDto projectDtoSecond;



    @Captor
    private ArgumentCaptor<Project> captor;

    private Project project;
    private Long parentId;
    private Long projectId;
    private Project parent;
    private ProjectDto projectDtoFirst;
    private S3Service s3Service;
    private PicProcessor picProcessor;
    private MultipartFile multipartFile;
    private ImageSize imageSize = new ImageSize();
    private ByteArrayOutputStream picture;
    private ObjectMetadata picMetadata;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {

        projectDtoFirst = new ProjectDto();
        project = Project.builder().status(ProjectStatus.CREATED).build();
        projectDtoSecond = ProjectDto.builder().ownerId(1L).name("Name").description("Description").build();
        imageSize.setOriginalHeight(100);
        imageSize.setOriginalWidth(100);

        projectValidator = Mockito.mock(ProjectValidator.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        projectMapper = Mockito.spy(ProjectMapperImpl.class);
        momentService = Mockito.mock(MomentService.class);
        validator = Mockito.mock(SubProjectValidator.class);
        s3Service = Mockito.mock(S3Service.class);
        picProcessor = Mockito.mock(PicProcessor.class);
        multipartFile = Mockito.mock(MultipartFile.class);
        mockProject = Mockito.mock(Project.class);
        picture = new ByteArrayOutputStream();
        picMetadata = new ObjectMetadata();
        projectDto = new ProjectDto();

        SubProjectNameFilter subProjectNameFilter = Mockito.mock(SubProjectNameFilter.class);
        SubProjectStatusFilter subProjectStatusFilter = Mockito.mock(SubProjectStatusFilter.class);
        filters = List.of(subProjectNameFilter, subProjectStatusFilter);

        projectService = Mockito.spy(new ProjectService(projectRepository, projectMapper,
                momentService, filters, validator, projectValidator, new ArrayList<>(), s3Service, picProcessor));


        parentId = 1L;
        projectId = 2L;

        parent = Project.builder()
                .id(1L)
                .visibility(PRIVATE)
                .children(new ArrayList<>())
                .build();
        parent.getChildren().add(Project.builder().status(COMPLETED).build());
    }

    @Test
    void testCreateSubProject() {
        Project parent = Project.builder().children(new ArrayList<>()).id(1L).visibility(PUBLIC).build();
        CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder().name("name").visibility(PUBLIC).build();
        Project projectToCreate = projectMapper.toEntity(subProjectDto);
        projectToCreate.setParentProject(parent);
        projectToCreate.setStatus(CREATED);

        when(projectRepository.getProjectById(parentId)).thenReturn(parent);
        List<Project> children = List.of(projectToCreate);

        projectService.createSubProject(parentId, subProjectDto);
        assertEquals(children, parent.getChildren());
        verify(projectRepository, times(1)).save(captor.capture());
        assertEquals(projectToCreate, captor.getValue());
    }

    @Test
    void testCreateSubProjectWhenParentNotExist() {
        when(projectRepository.getProjectById(parentId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> projectService.createSubProject(parentId, new CreateSubProjectDto()));
    }

    @Test
    void testUpdateSubProject() {
        Project projectToUpdate = Project.builder()
                .visibility(PRIVATE)
                .status(IN_PROGRESS)
                .parentProject(parent).build();
        ProjectDto projectDto = ProjectDto.builder()
                .name("name")
                .description("desc")
                .visibility(PRIVATE)
                .status(COMPLETED).build();

        parent.getChildren().add(projectToUpdate);

        when(projectRepository.getProjectById(projectId)).thenReturn(projectToUpdate);
        when(projectRepository.getProjectById(parentId)).thenReturn(parent);
        when(validator.isAllSubProjectsCompleted(parent)).thenReturn(true);

        projectService.updateSubProject(projectId, projectDto);
        MomentDto momentDto = MomentDto.builder()
                .title("Проект со всеми подзадачами выполенен")
                .projectId(projectId)
                .build();
        verify(momentService, times(1)).createMoment(momentDto);
        verify(projectMapper, times(1)).toDto(projectToUpdate);
    }

    @Test
    void testUpdateSubProjectWhenProjectNotExist() {
        when(projectRepository.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> projectService.updateSubProject(projectId, new ProjectDto()));
    }

    @Test
    void testGetSubProjectsWithPrivateVisibility() {
        Project project = Project.builder().visibility(PRIVATE).build();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertThrows(IllegalArgumentException.class,
                () -> projectService.getSubProjects(projectId, new SubProjectFilterDto()));
    }

    @Test
    void testGetSubProjects() {
        Project project1 = Project.builder().visibility(PUBLIC).build();
        Project project2 = Project.builder().visibility(PUBLIC).build();
        Project project3 = Project.builder().visibility(PRIVATE).build();
        List<Project> projects = new ArrayList<>();
        projects.add(project1);
        projects.add(project2);
        projects.add(project3);
        Project project = Project.builder().visibility(PUBLIC).children(projects).build();

        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        List<ProjectDto> actual = projectService.getSubProjects(projectId, new SubProjectFilterDto());

        assertEquals(2, actual.size());
    }

    @Test
    public void testCreate_IsRunSave() {
        when(projectMapper.toEntity(projectDtoFirst)).thenReturn(project);
        projectService.create(projectDtoFirst);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testUpdate_IsRunSave() {
        when(projectMapper.toEntity(projectDtoFirst)).thenReturn(project);
        projectService.create(projectDtoFirst);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    public void testGetAllProjects() {
        projectService.getAllProjects();
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetProjectById() {
        projectService.getProjectById(1L);
        verify(projectRepository, times(1)).getProjectById(1L);
    }

    @Test
    public void testUploadProjectPictureOnNullProject() {
        when(projectRepository.getProjectById(projectId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> projectService.uploadProjectPicture(projectId, multipartFile));
    }

    @Test
    public void testUploadProjectPictureWhenCoverImageIdIsNotNull() {
        when(projectRepository.getProjectById(projectId)).thenReturn(mockProject);
        when(mockProject.getCoverImageId()).thenReturn("1");
        when(picProcessor.getImageSize(multipartFile)).thenReturn(imageSize);
        when(picProcessor.getPicBaosByLength(multipartFile)).thenReturn(picture);
        when(picProcessor.getPicMetaData(multipartFile, picture, imageSize.getOriginalWidth(), imageSize.getOriginalHeight())).thenReturn(picMetadata);

        projectService.uploadProjectPicture(projectId, multipartFile);

        verify(projectService, times(1)).deleteProfilePicture(projectId);
        verify(picProcessor, times(1)).getImageSize(multipartFile);
        verify(picProcessor, times(1)).getPicBaosByLength(multipartFile);
        verify(picProcessor, times(1)).getPicMetaData(multipartFile, picture, imageSize.getOriginalWidth(), imageSize.getOriginalHeight());
    }

    @Test
    public void testUploadProjectPictureWhenCoverImageIdIsNull() {
        when(projectRepository.getProjectById(projectId)).thenReturn(mockProject);
        when(mockProject.getCoverImageId()).thenReturn(null);
        when(picProcessor.getImageSize(multipartFile)).thenReturn(imageSize);
        when(picProcessor.getPicBaosByLength(multipartFile)).thenReturn(picture);
        when(picProcessor.getPicMetaData(multipartFile, picture, imageSize.getOriginalWidth(), imageSize.getOriginalHeight())).thenReturn(picMetadata);

        projectService.uploadProjectPicture(projectId, multipartFile);

        verify(projectService, never()).deleteProfilePicture(projectId);
        verify(picProcessor, times(1)).getImageSize(multipartFile);
        verify(picProcessor, times(1)).getPicBaosByLength(multipartFile);
        verify(picProcessor, times(1)).getPicMetaData(multipartFile, picture, imageSize.getOriginalWidth(), imageSize.getOriginalHeight());
    }

    @Test
    public void testGetProjectPictureSuccess() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        doNothing().when(projectValidator).checkExistPicId(project);
        byte[] picBytes = new byte[]{1, 2, 3, 4, 5};
        InputStream inputStreamPic = new ByteArrayInputStream(picBytes);
        when(s3Service.getPicture(project.getCoverImageId())).thenReturn(inputStreamPic);

        ResponseEntity<byte[]> response = projectService.getProjectPicture(projectId);

        verify(projectValidator, times(1)).checkExistPicId(project);
        verify(s3Service, times(1)).getPicture(project.getCoverImageId());

        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        assert Objects.equals(response.getHeaders().getContentType(), MediaType.IMAGE_JPEG);
        assert response.getBody().length == picBytes.length;
    }

    @Test
    public void testGetProjectPictureIOException() throws IOException {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        doNothing().when(projectValidator).checkExistPicId(project);
        InputStream inputStreamPic = mock(InputStream.class);
        when(s3Service.getPicture(project.getCoverImageId())).thenReturn(inputStreamPic);
        doThrow(new IOException()).when(inputStreamPic).readAllBytes();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.getProjectPicture(projectId);
        });

        verify(projectValidator, times(1)).checkExistPicId(project);
        verify(s3Service, times(1)).getPicture(project.getCoverImageId());
        verify(inputStreamPic, times(1)).readAllBytes();

        assert exception.getCause() instanceof IOException;
    }

    @Test
    public void testDeleteProfilePictureSuccess() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        doNothing().when(projectValidator).checkExistPicId(project);
        doNothing().when(s3Service).deletePicture(project.getCoverImageId());
        when(projectMapper.toDto(project)).thenReturn(projectDto);

        ProjectDto result = projectService.deleteProfilePicture(projectId);

        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(projectValidator, times(1)).checkExistPicId(project);
        verify(s3Service, times(1)).deletePicture(project.getCoverImageId());
        verify(projectMapper, times(1)).toDto(project);

        assert result == projectDto;
        assert project.getCoverImageId() == null;
    }

    @Test
    public void testDeleteProfilePictureWhenProjectNotFound() {
        when(projectRepository.getProjectById(projectId)).thenThrow(new RuntimeException("Project not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            projectService.deleteProfilePicture(projectId);
        });

        assert exception.getMessage().equals("Project not found");

        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(projectValidator, never()).checkExistPicId(any());
        verify(s3Service, never()).deletePicture(any());
        verify(projectMapper, never()).toDto(any());
    }
}