package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.resource.S3Service;
import faang.school.projectservice.validator.ProjectCoverImageValidator;
import faang.school.projectservice.validator.ProjectServiceValidator;
import faang.school.projectservice.validator.util.image.MultipartImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;

@Slf4j
@Service
public class ProjectService {
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ProjectCoverImageValidator projectCoverImageValidator;
    private final ProjectServiceValidator projectServiceValidator;
    private final ResourceService resourceService;

    @Lazy
    public ProjectService(S3Service s3Service, ProjectRepository projectRepository,
                          ProjectCoverImageValidator projectCoverImageValidator,
                          ProjectServiceValidator projectServiceValidator, ResourceService resourceService) {
        this.s3Service = s3Service;
        this.projectRepository = projectRepository;
        this.projectCoverImageValidator = projectCoverImageValidator;
        this.projectServiceValidator = projectServiceValidator;
        this.resourceService = resourceService;
    }

    @Transactional
    public void updateStorageSize(Long projectId, BigInteger storageSize) {
        Project project = projectRepository.getProjectById(projectId);
        project.setStorageSize(storageSize);

        projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public ResourceDownloadDto getCoverImage(long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        String imageKey = projectServiceValidator.getCoverImageId(project);
        S3Object s3Object = s3Service.download(imageKey);
        try {
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
            log.error(e.getMessage());
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteCoverImage(long ownerId, long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        projectServiceValidator.projectOwner(project, ownerId);
        String imageKey = projectServiceValidator.getCoverImageId(project);

        projectServiceValidator.getResourceByKey(project, imageKey).ifPresent(resource -> {
            s3Service.delete(imageKey);
            resourceService.deleteResource(resource);

            project.setCoverImageId(null);
            long newStorageSize = project.getStorageSize().longValue() - resource.getSize().longValue();
            project.setStorageSize(BigInteger.valueOf(newStorageSize));
            projectRepository.save(project);
        });
    }

    @Transactional
    public void addCoverImage(long ownerId, long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        projectServiceValidator.projectOwner(project, ownerId);

        MultipartImage multipartImage = projectCoverImageValidator.validate(file);
        updateStorageByCoverImage(project, multipartImage);

        Resource projectCoverImageResource = s3Service.uploadProjectCoverImage(multipartImage, project);
        project.setCoverImageId(projectCoverImageResource.getKey());

        resourceService.saveResource(projectCoverImageResource);
        projectRepository.save(project);
    }

    private void updateStorageByCoverImage(Project project, MultipartFile multipartFile) {
        projectServiceValidator.getResourceByKey(project, project.getCoverImageId()).ifPresentOrElse(
                resource -> {
                    long newSize = projectServiceValidator.validResourceSize(project, multipartFile, resource);
                    s3Service.delete(resource.getKey());
                    resourceService.deleteResource(resource);
                    project.setStorageSize(BigInteger.valueOf(newSize));
                }, () -> {
                    long newSize = projectServiceValidator.validResourceSize(project, multipartFile, null);
                    project.setStorageSize(BigInteger.valueOf(newSize));
                });
    }
}
