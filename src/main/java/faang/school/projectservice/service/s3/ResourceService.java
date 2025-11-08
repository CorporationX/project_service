package faang.school.projectservice.service.s3;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.project.AccessDeniedException;
import faang.school.projectservice.exception.s3.FileNotSavedException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResourceService {

    public static final String UNKNOWN_FILE_NAME = "Unknown";
    public static final String UNKNOWN_CONTENT_TYPE = "unknown";

    @Value("${services.s3.resourceBucketName}")
    private String bucketName;

    private final S3Client s3Client;
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    @Transactional
    public long upload(Long projectId, MultipartFile file) {
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse(UNKNOWN_FILE_NAME);
        long fileSize = file.getSize();
        String contentType = Optional.ofNullable(file.getContentType()).orElse(UNKNOWN_CONTENT_TYPE);
        String key = String.format("projectId_%d/%s", projectId, fileName);
        long resourceId;

        Optional<Resource> existingResource = resourceRepository.findByKey(key);
        if (existingResource.isPresent()) {
            resourceId = existingResource.get().getId();
            updateResource(existingResource.get(), projectId, BigInteger.valueOf(fileSize), contentType);
        } else {
            resourceId = createResource(projectId, BigInteger.valueOf(fileSize), fileName, key, contentType);
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(fileSize)
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(inputStream, fileSize));
            log.info("File \"{}\" was saved to s3", key);
        } catch (RuntimeException | IOException e) {
            throw new FileNotSavedException(
                    String.format("Error when saving a file to S3 using the key: %s. Error message: %s",
                            key, e.getMessage()));
        }
        return resourceId;
    }

    private void updateResource(Resource resource,
                                long projectId,
                                BigInteger fileSize,
                                String contentType) {
        updateProjectStorageSize(projectId, fileSize.subtract(resource.getSize()));

        TeamMember teamMember = getTeamMember(projectId);

        resource.setSize(fileSize);
        resource.setAllowedRoles(new ArrayList<>(teamMember.getRoles()));
        resource.setType(ResourceType.getResourceType(contentType));
        resource.setUpdatedBy(teamMember);

        resourceRepository.save(resource);
        log.info("Resource was updated, id: {}", resource.getId());
    }

    private long createResource(long projectId,
                                BigInteger fileSize,
                                String fileName,
                                String key,
                                String contentType) {
        Project project = updateProjectStorageSize(projectId, fileSize);

        TeamMember teamMember = getTeamMember(projectId);

        Resource resourceToSave = Resource.builder()
                .name(fileName)
                .key(key)
                .size(fileSize)
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .type(ResourceType.getResourceType(contentType))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .project(project)
                .build();
        Resource resource = resourceRepository.save(resourceToSave);
        log.info("Resource was saved, id: {}", resource.getId());
        return resource.getId();
    }

    private TeamMember getTeamMember(long projectId) {
        long userId = userContext.getUserId();
        return Optional.ofNullable(teamMemberRepository.findByUserIdAndProjectId(userId, projectId))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("The TeamMember was not found by userId: %d and projectId: %d",
                                userId, projectId)));
    }

    private Project updateProjectStorageSize(long projectId, BigInteger fileSize) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        checkStorageLimit(project, fileSize);
        project.setStorageSize(project.getStorageSize().add(fileSize));
        projectRepository.save(project);
        log.info("Storage size was updated to {} for projectId: {}", project.getStorageSize(), projectId);
        return project;
    }

    private void checkStorageLimit(Project project, BigInteger fileSize) {
        if (project.getStorageSize().add(fileSize)
                .compareTo(project.getMaxStorageSize()) > 0) {
            throw new IllegalStateException(String.format("Storage quota (%d) exceeded for projectId: %d",
                    project.getMaxStorageSize(),
                    project.getId()));
        }
    }

    @Transactional
    public void delete(Long projectId, Long resourceId) {
        Resource resource = resourceRepository.getByIdOrThrow(resourceId);
        if (resource.getStatus() == ResourceStatus.DELETED) {
            return;
        }
        TeamMember teamMember = getTeamMember(projectId);
        validatePermissionToDelete(resource, teamMember);

        String key = resource.getKey();
        updateProjectStorageSize(projectId, resource.getSize().negate());

        resource.setKey("");
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);
        resourceRepository.save(resource);
        log.info("Resource {} was marked as deleted", resource.getId());


        try {
            DeleteObjectRequest del = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(del);
        } catch (Exception e) {
            throw new FileNotSavedException(
                    String.format("Error when deleting a file on S3 using the key: %s. Error message: %s",
                            key, e.getMessage()));
        }
    }

    private void validatePermissionToDelete(Resource resource, TeamMember teamMember) {
        if (!(resource.getCreatedBy().equals(teamMember)
                || teamMember.getRoles().contains(TeamRole.MANAGER))) {
            throw new AccessDeniedException("Only the creator of the file or the project manager can delete the file.");
        }
    }
}
