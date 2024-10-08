package faang.school.projectservice.service.project;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCoverServiceTest {

    private static final Long PROJECT_ID = 1L;
    private static final String COVER_IMAGE_ID = "cover123";
    private static final String  RESOURCE_KEY = "123";
    private static final String OLD_RESOURCE_KEY = "321";

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

            byte[] resizedImage = new byte[]{};

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(imageService.resizeImage(imageFile)).thenReturn(resizedImage);
            when(resourceService.putResource(any(), any(), any())).thenReturn(resource);
            doNothing().when(s3Service).uploadFile(any(), any(), any(), anyInt(), any());

            ProjectCoverDto result = projectCoverService.uploadCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertNotNull(result.getCoverId());

            verify(resourceService).putResource(anyString(), eq(imageFile), eq(ResourceType.IMAGE));
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
            project.setCoverImageId(OLD_RESOURCE_KEY);

            Resource resource = new Resource();
            resource.setKey(RESOURCE_KEY);

            byte[] resizedImage = new byte[]{};

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(imageService.resizeImage(imageFile)).thenReturn(resizedImage);
            when(resourceService.putResource(any(), any(), any())).thenReturn(resource);
            doNothing().when(s3Service).uploadFile(any(), any(), any(), anyInt(), any());

            ProjectCoverDto result = projectCoverService.changeCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertNotNull(result.getCoverId());

            verify(resourceService).markResourceAsDeleted(anyString());
            verify(s3Service).removeFileById(anyString());

            verify(resourceService).putResource(any(), eq(imageFile), eq(ResourceType.IMAGE));
        }
    }

    @Nested
    @DisplayName("When retrieving a cover")
    class GetCover {

        @Test
        @DisplayName("should return FileData if the cover exists")
        void whenGetCoverThenReturnFileData() {
            Project project = new Project();
            project.setId(PROJECT_ID);
            project.setCoverImageId(COVER_IMAGE_ID);

            FileData fileData = mock(FileData.class);

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(s3Service.getFileById(COVER_IMAGE_ID)).thenReturn(fileData);

            FileData result = projectCoverService.getCover(PROJECT_ID);

            assertNotNull(result);
            assertEquals(fileData, result);
        }

        @Test
        @DisplayName("should return null if no cover exists")
        void whenNoCoverExistsThenReturnNull() {
            Project project = new Project();
            project.setId(PROJECT_ID);
            project.setCoverImageId(COVER_IMAGE_ID);

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);

            FileData result = projectCoverService.getCover(PROJECT_ID);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("When deleting a cover")
    class DeleteCover {

        @Test
        @DisplayName("should delete the cover and update the project")
        void whenDeleteCoverThenRemoveCoverAndSaveProject() {
            Project project = new Project();
            project.setId(PROJECT_ID);
            project.setCoverImageId(RESOURCE_KEY);

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);

            projectCoverService.removeCover(PROJECT_ID);

            assertNull(project.getCoverImageId());

            verify(resourceService).markResourceAsDeleted(RESOURCE_KEY);
            verify(s3Service).removeFileById(RESOURCE_KEY);
            verify(projectRepository).save(project);
        }
    }
}
