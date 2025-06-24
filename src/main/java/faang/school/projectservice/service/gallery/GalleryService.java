package faang.school.projectservice.service.gallery;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.resource.ResourceService;
import faang.school.projectservice.service.s3.S3AsyncService;
import faang.school.projectservice.util.S3FileUtil;
import faang.school.projectservice.validation.gallery.GalleryValidation;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import static faang.school.projectservice.validation.StorageValidation.storageSizeNotExceededValidation;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class GalleryService {

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ResourceService resourceService;
    private final ResourceRepository resourceRepository;
    private final GalleryValidation galleryValidation;
    private final S3AsyncService s3Service;
    private final S3FileUtil s3FileUtil;

    @Transactional(readOnly = true)
    public S3FileDto getGalleryItem(long projectId, long resourceId) {
        Project project = projectService.getProjectById(projectId);
        galleryValidation.validateProjectIsPublic(project);

        Resource resource = resourceService.getResourceById(resourceId);
        galleryValidation.validateFileIsProjectGalleryItem(project, resource);

        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional(readOnly = true)
    public Page<Resource> getGallery(Long projectId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Project project = projectService.getProjectById(projectId);
        galleryValidation.validateProjectIsPublic(project);

        Page<String> keys = projectRepository.findFileKeysByProjectId(projectId, pageable);

        return keys.map(resourceService::getResourceByKey);
    }

    @Transactional
    public void addGalleryAsync(Long projectId, List<MultipartFile> files) {
        galleryValidation.validateAllowedContentTypes(files);
        galleryValidation.validateGalleryLimit(projectId, files.size());

        Project project = projectService.getProjectById(projectId);

        BigInteger additionalSize = files.stream()
                .map(file -> BigInteger.valueOf(file.getSize()))
                .reduce(BigInteger.ZERO, BigInteger::add);

        BigInteger newStorageSize = Objects.requireNonNull(project.getStorageSize()).add(additionalSize);
        storageSizeNotExceededValidation(newStorageSize, project.getMaxStorageSize());

        project.setStorageSize(newStorageSize);

        List<Resource> uploadingFiles = files.stream()
                .map(file -> resourceService.uploadResourceAsync(project, file))
                .toList();

        uploadingFiles.forEach(resource -> project.getGalleryFileKeys().add(resource.getKey()));

        projectRepository.save(project);
    }

    @Transactional
    public void deleteFile(long projectId, long resourceId) {
        Project project = projectService.getProjectById(projectId);

        Resource resource = resourceService.getResourceById(resourceId);
        galleryValidation.validateFileIsProjectGalleryItem(project, resource);

        project.getGalleryFileKeys().remove(resource.getKey());

        resourceService.deleteResource(projectId, resourceId);

        projectRepository.save(project);
    }
}