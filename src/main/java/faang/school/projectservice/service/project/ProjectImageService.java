package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.resource.S3Service;
import faang.school.projectservice.validator.ProjectCoverImageValidator;
import faang.school.projectservice.validator.ProjectServiceValidator;
import faang.school.projectservice.validator.util.image.MultipartImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectImageService {
    private final S3Service s3Service;
    private final ResourceService resourceService;
    private final ProjectService projectService;
    private final ProjectServiceValidator projectServiceValidator;
    private final ProjectCoverImageValidator projectCoverImageValidator;

    public void addCoverImage(long ownerId, long projectId, MultipartFile file) {
        Project project = projectService.findById(projectId);
        projectServiceValidator.projectOwner(project, ownerId);

        MultipartImage multipartImage = projectCoverImageValidator.validate(file);
        updateStorageByCoverImage(project, multipartImage);

        Resource projectCoverImageResource = s3Service.uploadProjectCoverImage(multipartImage, project);
        project.setCoverImageId(projectCoverImageResource.getKey());

        resourceService.saveResource(projectCoverImageResource);
        projectService.save(project);
    }

    public ResourceDownloadDto getCoverImage(long id) {
        Project project = projectService.findById(id);
        String imageKey = projectServiceValidator.coverImageId(project);
        try (S3Object s3Object = s3Service.download(imageKey)) {
            String contentType = s3Object.getObjectMetadata().getContentType();
            ContentDisposition contentDisposition = ContentDisposition.inline()
                    .filename(imageKey)
                    .build();
            return ResourceDownloadDto.builder()
                    .bytes(s3Object.getObjectContent().readAllBytes())
                    .type(MediaType.valueOf(contentType))
                    .contentDisposition(contentDisposition)
                    .build();
        } catch (IOException e) {
            log.error("", e);
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteCoverImage(long ownerId, long projectId) {
        Project project = projectService.findById(projectId);
        projectServiceValidator.projectOwner(project, ownerId);
        String imageKey = projectServiceValidator.coverImageId(project);

        projectServiceValidator.getResourceByKey(project, imageKey).ifPresent(resource ->
                deleteResourceEverywhere(project, resource));
    }

    private void deleteResourceEverywhere(Project project, Resource resource) {
        project.setCoverImageId(null);
        long newStorageSize = project.getStorageSize().longValue() - resource.getSize().longValue();
        project.setStorageSize(BigInteger.valueOf(newStorageSize));
        project.getResources().remove(resource);

        projectService.save(project);
        s3Service.delete(resource.getKey());
        resourceService.deleteResource(resource);
    }

    private void updateStorageByCoverImage(Project project, MultipartFile file) {
        projectServiceValidator.getResourceByKey(project, project.getCoverImageId()).ifPresent(resource ->
                deleteResourceEverywhere(project, resource));
        long newSize = projectServiceValidator.validResourceSize(project, file.getSize());
        project.setStorageSize(BigInteger.valueOf(newSize));
    }
}
