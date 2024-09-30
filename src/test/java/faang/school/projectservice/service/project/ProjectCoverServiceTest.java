package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.image.FileData;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.model.Project;
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
    private static final String OLD_COVER_ID = "oldCoverId";
    private static final String NEW_COVER_ID = "newCover123";
    private static final String COVER_IMAGE_ID = "cover123";

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

            byte[] resizedImage = new byte[]{};
            String coverImageId = COVER_IMAGE_ID;

            doNothing().when(imageValidator).validateMaximumSize(anyLong());
            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(imageService.resizeImage(imageFile)).thenReturn(resizedImage);
            when(s3Service.uploadObject(any(), any(), anyInt(), any()))
                    .thenReturn(coverImageId);

            ProjectCoverDto result = projectCoverService.uploadCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertEquals(coverImageId, result.getCoverId());

            verify(resourceService).putResource(imageFile, ResourceType.IMAGE, coverImageId);
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
            project.setCoverImageId(OLD_COVER_ID);

            byte[] resizedImage = new byte[]{};
            String newCoverImageId = NEW_COVER_ID;

            doNothing().when(imageValidator).validateMaximumSize(anyLong());

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);
            when(imageService.resizeImage(imageFile)).thenReturn(resizedImage);
            when(s3Service.uploadObject(any(), any(), anyInt(), any()))
                    .thenReturn(newCoverImageId);

            ProjectCoverDto result = projectCoverService.changeCover(PROJECT_ID, imageFile);

            assertNotNull(result);
            assertEquals(PROJECT_ID, result.getProjectId());
            assertEquals(newCoverImageId, result.getCoverId());

            verify(resourceService).markResourceAsDeleted(OLD_COVER_ID);
            verify(s3Service).removeObjectByKey(OLD_COVER_ID);

            verify(resourceService).putResource(imageFile, ResourceType.IMAGE, newCoverImageId);
            verify(projectRepository).save(project);

            assertEquals(newCoverImageId, project.getCoverImageId());
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
            when(s3Service.getObjectByKey(COVER_IMAGE_ID)).thenReturn(fileData);

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
            project.setCoverImageId(COVER_IMAGE_ID);

            when(projectService.getProjectById(PROJECT_ID)).thenReturn(project);

            projectCoverService.deleteCover(PROJECT_ID);

            assertNull(project.getCoverImageId());

            verify(resourceService).markResourceAsDeleted(COVER_IMAGE_ID);
            verify(s3Service).removeObjectByKey(COVER_IMAGE_ID);
            verify(projectRepository).save(project);
        }
    }
}
