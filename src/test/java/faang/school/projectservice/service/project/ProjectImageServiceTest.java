package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.resource.S3Service;
import faang.school.projectservice.validator.ProjectCoverImageValidator;
import faang.school.projectservice.validator.ProjectServiceValidator;
import faang.school.projectservice.validator.util.image.MultipartImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ContentDisposition;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.util.project.ProjectFabric.buildProject;
import static faang.school.projectservice.util.project.ProjectFabric.buildProjectCoverImageId;
import static faang.school.projectservice.util.project.ProjectFabric.buildResource;
import static faang.school.projectservice.util.project.ProjectFabric.buildResourceDownloadDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectImageServiceTest {
    private static final long USER_ID = 1L;
    private static final long PROJECT_ID = 1L;
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final BigInteger STORAGE_SIZE = BigInteger.valueOf(1);
    private static final String COVER_ID = "cover";
    private static final byte[] BYTES = "image content".getBytes();
    private static final long PROJECT_NEW_SIZE = 2L;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectServiceValidator projectServiceValidator;

    @Mock
    private ProjectCoverImageValidator projectCoverImageValidator;

    @InjectMocks
    private ProjectImageService projectImageService;

    @Test
    @DisplayName("Add cover image by project have old cover image ")
    void testAddCoverImageAlreadyHaveCoverImage() {
        MultipartImage multipartImage = mock(MultipartImage.class);
        Resource resource = buildResource(COVER_ID, STORAGE_SIZE);
        Project project = buildProjectCoverImageId(PROJECT_ID, COVER_ID, STORAGE_SIZE, new ArrayList<>(List.of(resource)));

        when(projectService.findById(PROJECT_ID)).thenReturn(project);
        when(projectCoverImageValidator.validate(multipartImage)).thenReturn(multipartImage);
        when(projectServiceValidator.getResourceByKey(project, COVER_ID)).thenReturn(Optional.of(resource));

        when(projectServiceValidator.validResourceSize(project, multipartImage.getSize())).thenReturn(PROJECT_NEW_SIZE);
        when(s3Service.uploadProjectCoverImage(multipartImage, project)).thenReturn(resource);

        projectImageService.addCoverImage(USER_ID, PROJECT_ID, multipartImage);

        verify(projectService, times(2)).save(project);
        verify(s3Service).delete(COVER_ID);
        verify(resourceService).deleteResource(resource);
        verify(resourceService).saveResource(resource);
    }

    @Test
    @DisplayName("Add cover image by project don't have old cover image ")
    void testAddCoverImageAlreadyDontHaveOdlCoverImage() {
        MultipartImage multipartImage = mock(MultipartImage.class);
        Resource resource = buildResource(COVER_ID, STORAGE_SIZE);
        Project project = buildProjectCoverImageId(PROJECT_ID, COVER_ID, STORAGE_SIZE,
                new ArrayList<>(List.of(resource)));

        when(projectService.findById(PROJECT_ID)).thenReturn(project);
        when(projectCoverImageValidator.validate(multipartImage)).thenReturn(multipartImage);
        when(projectServiceValidator.getResourceByKey(project, COVER_ID)).thenReturn(Optional.empty());

        when(projectServiceValidator.validResourceSize(project, multipartImage.getSize())).thenReturn(PROJECT_NEW_SIZE);
        when(s3Service.uploadProjectCoverImage(multipartImage, project)).thenReturn(resource);

        projectImageService.addCoverImage(USER_ID, PROJECT_ID, multipartImage);

        verify(projectService, times(1)).save(project);
        verify(resourceService).saveResource(resource);
    }

    @Test
    @DisplayName("Get cover image successful")
    void testGetCoverImageSuccessful() {
        Project project = buildProject(PROJECT_ID);
        ContentDisposition contentDisposition = ContentDisposition.inline()
                .filename(COVER_ID)
                .build();
        S3Object s3Object = mock(S3Object.class);
        S3ObjectInputStream s3ObjectInputStream =
                new S3ObjectInputStream(new ByteArrayInputStream(BYTES), null);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(CONTENT_TYPE);
        ResourceDownloadDto resourceDownloadDto = buildResourceDownloadDto(BYTES, CONTENT_TYPE, contentDisposition);

        when(projectService.findById(PROJECT_ID)).thenReturn(project);
        when(projectServiceValidator.coverImageId(project)).thenReturn(COVER_ID);
        when(s3Service.download(COVER_ID)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
        when(s3Object.getObjectMetadata()).thenReturn(metadata);

        assertThat(projectImageService.getCoverImage(PROJECT_ID))
                .isNotNull()
                .isEqualTo(resourceDownloadDto);
    }

    @Test
    @DisplayName("Delete cover image successful")
    void testDeleteCoverImageSuccessful() {
        Resource resource = buildResource(COVER_ID, STORAGE_SIZE);
        Project project = buildProjectCoverImageId(PROJECT_ID, COVER_ID, STORAGE_SIZE,
                new ArrayList<>(List.of(resource)));

        when(projectService.findById(PROJECT_ID)).thenReturn(project);
        when(projectServiceValidator.coverImageId(project)).thenReturn(COVER_ID);
        when(projectServiceValidator.getResourceByKey(project, COVER_ID)).thenReturn(Optional.of(resource));

        projectImageService.deleteCoverImage(USER_ID, PROJECT_ID);

        verify(projectService).save(project);
        verify(s3Service).delete(COVER_ID);
        verify(resourceService).deleteResource(resource);
    }
}