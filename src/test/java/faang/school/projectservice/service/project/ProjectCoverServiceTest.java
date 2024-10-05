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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectCoverServiceTest {

    private static final Long PROJECT_ID = 1L;
    private static final String COVER_IMAGE_ID = "cover123";
    private static final Long RESOURCE_ID = 123L;
    private static final Long OLD_RESOURCE_ID = 321L;

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
            resource.setId(RESOURCE_ID);

            byte[] resizedImage = new byte[]{};

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(imageService.resizeImage(imageFile)).thenReturn(resizedImage);
            when(resourceService.putResource(any(), any())).thenReturn(resource);
            doNothing().when(s3Service).uploadFile(any(), any(), any(), anyInt(), any());

            ProjectCoverDto result = projectCoverService.uploadCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertEquals(RESOURCE_ID, result.getCoverId());

            verify(resourceService).putResource(imageFile, ResourceType.IMAGE);
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
            project.setCoverImageId(String.valueOf(OLD_RESOURCE_ID));

            Resource resource = new Resource();
            resource.setId(RESOURCE_ID);

            byte[] resizedImage = new byte[]{};

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(imageService.resizeImage(imageFile)).thenReturn(resizedImage);
            when(resourceService.putResource(any(), any())).thenReturn(resource);
            doNothing().when(s3Service).uploadFile(any(), any(), any(), anyInt(), any());

            ProjectCoverDto result = projectCoverService.changeCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertEquals(RESOURCE_ID, result.getCoverId());

            verify(resourceService).markResourceAsDeleted(OLD_RESOURCE_ID);
            verify(s3Service).removeFileById(OLD_RESOURCE_ID);

            verify(resourceService).putResource(imageFile, ResourceType.IMAGE);
            verify(projectRepository).save(project);
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
            when(s3Service.getFileByKey(COVER_IMAGE_ID)).thenReturn(fileData);

            FileData result = projectCoverService.getCover(PROJECT_ID);

            assertNotNull(result);
            assertEquals(fileData, result);
        }

        @Test
        @DisplayName("should return null if no cover exists")
        void whenNoCoverExistsThenReturnNull() {
            Project project = new Project();
            project.setId(PROJECT_ID);

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
            project.setCoverImageId(String.valueOf(RESOURCE_ID));

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);

            projectCoverService.deleteCover(PROJECT_ID);

            assertNull(project.getCoverImageId());

            verify(resourceService).markResourceAsDeleted(RESOURCE_ID);
            verify(s3Service).removeFileById(RESOURCE_ID);
            verify(projectRepository).save(project);
        }
    }
}
