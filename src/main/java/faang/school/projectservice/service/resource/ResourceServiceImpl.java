package faang.school.projectservice.service.resource;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.resource.ExtendedResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.SizeExceeded;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.s3.S3Service;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final UserServiceClient userServiceClient;
    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);
        log.info("The file {} has been successfully verified for add", file.getOriginalFilename());

        String folder = project.getId() + project.getName();
        Resource resource = getResource(file, folder);

        resource.setProject(project);
        resource = resourceRepository.save(resource);
        log.info("The resource with {}id has been successfully saved in repository", resource.getId());

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("The project with {}id has been successfully saved in repository", project.getId());

        return resourceMapper.toDto(resource);
    }

    private Resource getResource(MultipartFile file, String folder) {
        String key = s3Service.uploadFile(file, folder);

        return Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .build();
    }

    @Transactional
    public String uploadProjectCover(Long projectId, MultipartFile file) {
        validateImageFile(file);

        String key = s3Service.uploadCover(file);

        Project project = projectRepository.getProjectById(projectId);

        if (project.getCoverImageId() != null && !project.getCoverImageId().isEmpty()) {
            s3Service.deleteFile(project.getCoverImageId());
            log.info("Deleted old cover image with key: {}", project.getCoverImageId());
        }

        project.setCoverImageId(key);
        projectRepository.save(project);

        return key;
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileException(ErrorMessage.FILE_NOT_PROVIDED);
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileException(ErrorMessage.FILE_SIZE_EXCEEDED);
        }

        String contentType = Objects.requireNonNull(file.getContentType(), "File content type is missing.");
        if (!contentType.startsWith("image/")) {
            throw new FileException(ErrorMessage.INVALID_FILE_TYPE);
        }
    }

    @Transactional
    public ExtendedResourceDto downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Resource with " + resourceId + " id not found"));
        InputStream inputStream = s3Service.downloadFile(resource.getKey());
        InputStreamResource resourceStream = new InputStreamResource(inputStream);
        MediaType mediaType = getMediaTypeForResourceType(resource.getType());
        long contentLength = resource.getSize().longValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentLength(contentLength);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(resource.getName())
                .build());

        return ExtendedResourceDto.builder()
                .resourceStream(resourceStream)
                .headers(headers)
                .build();
    }

    private MediaType getMediaTypeForResourceType(ResourceType resourceType) {
        return switch (resourceType) {
            case PDF -> MediaType.APPLICATION_PDF;
            case IMAGE -> MediaType.IMAGE_JPEG;
            case VIDEO -> MediaType.valueOf("video/mp4");
            case AUDIO -> MediaType.valueOf("audio/mpeg");
            case MSWORD -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case MSEXCEL -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case TEXT -> MediaType.TEXT_PLAIN;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    @Transactional
    public void deleteResource(long resourceId, long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with " + userId + " id not found");
        }

        Resource resource = resourceRepository.getReferenceById(resourceId);
        String key = resource.getKey();

        s3Service.deleteFile(key);
        resourceRepository.delete(resource);

        Project project = resource.getProject();
        BigInteger newSize = project.getStorageSize().subtract(resource.getSize());
        project.setStorageSize(newSize);
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file) {
        Resource resourceFromDB = getResourceWithCheckedPermissions(resourceId, userId);
        log.info("User with {}id has permissions to update the file", userId);

        Project project = resourceFromDB.getProject();

        BigInteger newStorageSize = project.getStorageSize()
                .add(BigInteger.valueOf(file.getSize()))
                .subtract(resourceFromDB.getSize());

        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);
        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resourceFromDB.getKey());

        Resource resource = getResource(file, folder);

        resourceFromDB.setKey(resource.getKey());
        resourceFromDB.setSize(resource.getSize());
        resourceFromDB.setUpdatedAt(resource.getUpdatedAt());
        resourceFromDB.setName(resource.getName());
        resourceFromDB.setType(resource.getType());

        resourceRepository.save(resourceFromDB);
        log.info("The resource with {} id has been successfully update", resourceFromDB.getId());

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("The project with {} id has been successfully update", project.getId());

        return resourceMapper.toDto(resourceFromDB);
    }

    private void checkStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (0 > maxStorageSize.compareTo(newStorageSize)) {
            throw new SizeExceeded(ErrorMessage.FILE_STORAGE_CAPACITY_EXCEEDED);
        }
    }

    private Resource getResourceWithCheckedPermissions(Long resourceId, Long userDtoId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Resource with %s id not found", resourceId)));
        var teamMember = resource.getCreatedBy();
        if (teamMember.getUserId().equals(userDtoId)) {
            return resource;
        } else {
            throw new IllegalArgumentException(String.format("User with %sid don't have permissions to update the file with %did", userDtoId, resourceId));
        }
    }
}
