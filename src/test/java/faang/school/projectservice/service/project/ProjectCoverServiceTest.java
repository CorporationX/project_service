package faang.school.projectservice.service.project;

import faang.school.projectservice.repository.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.config.minio.properties.ProjectCoverMinioProperties;
import faang.school.projectservice.exception.BadRequestException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.MinioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.constant.ImageTestConstants.IMAGE_INPUT_STREAM;
import static faang.school.projectservice.constant.ImageTestConstants.IMAGE_MOCK_MULTIPART_FILE;
import static faang.school.projectservice.constant.ProjectTestConstants.NOT_OWNER_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.OWNER_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.PROJECT_COVER_IMAGE_ID;
import static faang.school.projectservice.constant.ProjectTestConstants.TEST_PROJECT_ID;

@ExtendWith(MockitoExtension.class)
public class ProjectCoverServiceTest {

    @Mock
    private ProjectRepositoryAdapter projectRepositoryAdapter;

    @Mock
    private MinioService minioService;

    @Spy
    private ProjectCoverMinioProperties projectCoverMinioProperties;

    @Spy
    private UserContext userContext;

    @InjectMocks
    private ProjectCoverService projectCoverService;
    
    private Project testProject;

    @BeforeEach
    void init() {
        userContext.setUserId(OWNER_ID);

        projectCoverMinioProperties.setMaxSize(5242880);
        projectCoverMinioProperties.setCompressedOutputQuality(0.5);
        projectCoverMinioProperties.setCompressedOutputScale(1);
        projectCoverMinioProperties.setFolderName("projects_covers");
        
        testProject = Project.builder()
                .id(TEST_PROJECT_ID)
                .name("Test")
                .description("Test project")
                .ownerId(OWNER_ID)
                .visibility(ProjectVisibility.PUBLIC)
                .status(ProjectStatus.CREATED)
                .build();
    }

    @Test
    void addProjectCover_shouldThrowBadRequestException_whenTheUserIsNotTheOwnerOfTheProject() {
        userContext.setUserId(NOT_OWNER_ID);

        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectCoverService.addProjectCover(TEST_PROJECT_ID, IMAGE_MOCK_MULTIPART_FILE));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
    }

    @Test
    void addProjectCover_shouldThrowBadRequestException_whenProjectCoverImageIdIsNotNull() {
        userContext.setUserId(NOT_OWNER_ID);

        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        testProject.setCoverImageId(PROJECT_COVER_IMAGE_ID);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectCoverService.addProjectCover(TEST_PROJECT_ID, IMAGE_MOCK_MULTIPART_FILE));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
    }

    @Test
    void addProjectCover_shouldBeCompletedSuccessfully() {
        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        Mockito.doNothing().when(minioService).uploadFile(Mockito.anyString(), Mockito.any(),
                Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getSize()),
                Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getContentType()));

        projectCoverService.addProjectCover(TEST_PROJECT_ID, IMAGE_MOCK_MULTIPART_FILE);

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
        Mockito.verify(minioService, Mockito.times(1))
                .uploadFile(Mockito.anyString(), Mockito.any(),
                        Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getSize()),
                        Mockito.eq(IMAGE_MOCK_MULTIPART_FILE.getContentType()));
    }

    @Test
    void deleteProjectCover_shouldThrowBadRequestException_whenTheUserIsNotTheOwnerOfTheProject() {
        userContext.setUserId(NOT_OWNER_ID);

        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectCoverService.deleteProjectCover(TEST_PROJECT_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
    }

    @Test
    void deleteProjectCover_shouldThrowBadRequestException_whenProjectCoverImageIdIsNull() {
        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        Assertions.assertThrows(BadRequestException.class,
                () -> projectCoverService.deleteProjectCover(TEST_PROJECT_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
    }

    @Test
    void deleteProjectCover_shouldBeCompletedSuccessfully() {
        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        testProject.setCoverImageId(PROJECT_COVER_IMAGE_ID);

        Mockito.doNothing().when(minioService).deleteFile(PROJECT_COVER_IMAGE_ID);

        projectCoverService.deleteProjectCover(TEST_PROJECT_ID);

        Assertions.assertNull(testProject.getCoverImageId());

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
        Mockito.verify(minioService, Mockito.times(1))
                .deleteFile(PROJECT_COVER_IMAGE_ID);
    }

    @Test
    void getProjectCover_shouldBeCompletedSuccessfully() {
        Mockito.when(projectRepositoryAdapter.getById(TEST_PROJECT_ID)).thenReturn(testProject);

        testProject.setCoverImageId(PROJECT_COVER_IMAGE_ID);

        Mockito.when(minioService.getFile(PROJECT_COVER_IMAGE_ID))
                .thenReturn(IMAGE_INPUT_STREAM);

        Assertions.assertEquals(IMAGE_INPUT_STREAM, projectCoverService.getProjectCover(TEST_PROJECT_ID));

        Mockito.verify(projectRepositoryAdapter, Mockito.times(1)).getById(TEST_PROJECT_ID);
        Mockito.verify(minioService, Mockito.times(1))
                .getFile(PROJECT_COVER_IMAGE_ID);
    }
}
