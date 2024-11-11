package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.image.ImageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCoverServiceTest {

    private static final Long PROJECT_ID = 1L;
    private static final String PROJECT_NAME = "PROJECT_NAME";
    private static final String RESOURCE_KEY = "123";
    private static final String CONTENT_TYPE = "image/jpeg";

    @Mock
    private ImageService imageService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private ImageValidator imageValidator;

    @Mock
    private ResourceService resourceService;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private ProjectCoverService projectCoverService;

    @Nested
    @DisplayName("When uploading a new cover")
    class UploadCover {

        @Test
        @DisplayName("should upload and return ProjectCoverDto")
        void whenUploadCoverThenReturnProjectCoverDto() {
            Project project = new Project();
            project.setId(PROJECT_ID);

            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(resourceService.saveResource(any(), any(), any())).thenReturn(resource);
            doNothing().when(s3Service).saveObject(any(), any());

            ProjectCoverDto result = projectCoverService.uploadCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertNotNull(result.getCoverId());

            verify(resourceService).saveResource(eq(imageFile), any(), eq(ResourceType.IMAGE));
            verify(projectRepository).save(project);
        }
    }

    @Nested
    @DisplayName("When changing a cover")
    class ChangeCover {

        @Test
        @DisplayName("should replace the existing cover and return ProjectCoverDto")
        void whenChangeCoverThenReplaceAndReturnProjectCoverDto() {
            Project project = new Project();
            project.setId(PROJECT_ID);
            project.setName(PROJECT_NAME);
            project.setCoverImageId(RESOURCE_KEY);

            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(resourceService.saveResource(any(), any(), any())).thenReturn(resource);
            doNothing().when(s3Service).saveObject(any(), any());

            ProjectCoverDto result = projectCoverService.changeCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertNotNull(result.getCoverId());

            verify(resourceService).markResourceAsDeleted(anyString());
            verify(s3Service).deleteObject(eq(RESOURCE_KEY), eq(PROJECT_NAME));
            verify(resourceService).saveResource(eq(imageFile), any(), eq(ResourceType.IMAGE));
        }
    }

    @Nested
    @DisplayName("When retrieving a cover")
    class GetCover {

        @Test
        @DisplayName("should return FileData if the cover exists")
        void whenGetCoverThenReturnFileData() throws IOException {
            Project project = new Project();
            project.setId(PROJECT_ID);
            project.setName(PROJECT_NAME);
            project.setCoverImageId(RESOURCE_KEY);

            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);

            S3Object s3Object = mock(S3Object.class);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(CONTENT_TYPE);
            byte[] imageBytes = {1, 2, 3, 4};
            S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(new ByteArrayInputStream(imageBytes), null);

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(resourceService.getResourceByKey(RESOURCE_KEY)).thenReturn(resource);
            when(s3Service.getObject(resource)).thenReturn(s3Object);
            when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
            when(s3Object.getObjectMetadata()).thenReturn(metadata);

            FileData result = projectCoverService.getCover(PROJECT_ID);

            assertNotNull(result);
            assertEquals(CONTENT_TYPE, result.getContentType());
            assertArrayEquals(imageBytes, result.getData());
        }
    }

    @Nested
    @DisplayName("When deleting a cover")
    class DeleteCover {

        @Test
        @DisplayName("should delete the cover and updateCampaign the project")
        void whenDeleteCoverThenRemoveCoverAndSaveProject() {
            Project project = new Project();
            project.setId(PROJECT_ID);
            project.setName(PROJECT_NAME);
            project.setCoverImageId(RESOURCE_KEY);

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);

            projectCoverService.removeCover(PROJECT_ID);

            assertNull(project.getCoverImageId());

            verify(resourceService).markResourceAsDeleted(RESOURCE_KEY);
            verify(s3Service).deleteObject(eq(RESOURCE_KEY), eq(PROJECT_NAME));
            verify(projectRepository).save(project);
        }
    }
}
