package faang.school.projectservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CoverProjectServiceTest {
    private static final String MESSAGE_NO_IMAGE_IN_PROJECT = "There is no coverImage in the project";
    private static final String MESSAGE_PROJECT_NOT_IN_DB = "Project is not in database";
    private static final String MESSAGE_RESOURCE_NOT_IN_DB = "Resource not in database";
    private static final long VALID_ID = 1L;
    private static final String RANDOM_KEY = "key";
    private static final String RANDOM_TYPE = "img";
    private static final String TEST_STRING = "test";
    private static final int TEST_DATA = 111;
    private Project project;
    private Resource resource;
    private MockMultipartFile cover;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    @Mock
    private ImageService imageService;
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private ProjectJpaRepository projectRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private CoverProjectService service;

    @BeforeEach
    void setUp() {
        //Arrange
        project = new Project();
        project.setId(VALID_ID);
        project.setCoverImageId(RANDOM_KEY);
        resource = new Resource();
        cover = new MockMultipartFile(TEST_STRING, TEST_STRING.getBytes());
    }

    @Test
    public void testInvalidGetProject() {
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_PROJECT_NOT_IN_DB,
                assertThrows(RuntimeException.class, () -> service.uploadProjectCover(VALID_ID, cover)).getMessage());
    }

    @Test
    public void testValidUploadCover() {
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
        Mockito.when(imageService.resizeImage(Mockito.any())).thenReturn(new byte[]{TEST_DATA});
        //Assert
        service.uploadProjectCover(project.getId(), cover);
    }

    @Test
    public void testCoverImageIdIsNull() {
        //Arrange
        project.setCoverImageId(null);
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
        //Assert
        assertEquals(MESSAGE_NO_IMAGE_IN_PROJECT,
                assertThrows(RuntimeException.class, () -> service.changeProjectCover(VALID_ID, cover)).getMessage());
    }

    @Test
    public void testInvalidFindResourceByKey() {
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
        Mockito.when(resourceRepository.findResourceByKey(Mockito.any())).thenReturn(Optional.empty());
        //Assert
        assertEquals(MESSAGE_RESOURCE_NOT_IN_DB,
                assertThrows(RuntimeException.class, () -> service.changeProjectCover(VALID_ID, cover)).getMessage());
    }

    @Test
    public void testValidChangeCover() {
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
        Mockito.when(resourceRepository.findResourceByKey(Mockito.any())).thenReturn(Optional.of(resource));
        Mockito.when(imageService.resizeImage(Mockito.any())).thenReturn(new byte[]{TEST_DATA});
        //Assert
        service.changeProjectCover(project.getId(), cover);
    }

    @Test
    public void testValidGetCover() throws IOException {
        //Arrange
        S3Object s3Object = new S3Object();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(RANDOM_TYPE);
        s3Object.setObjectContent(new ByteArrayInputStream(TEST_STRING.getBytes()));
        s3Object.setObjectMetadata(objectMetadata);
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
        Mockito.when(amazonS3.getObject(bucketName, RANDOM_KEY)).thenReturn(s3Object);
        //Assert
        service.getProjectCover(project.getId());
    }

    @Test
    public void testValidDeleteCover() {
        //Act
        Mockito.when(projectRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(project));
        Mockito.when(resourceRepository.findResourceByKey(Mockito.any())).thenReturn(Optional.of(resource));
        //Assert
        service.deleteProjectCover(project.getId());
        Mockito.verify(projectRepository).save(project);
    }

}