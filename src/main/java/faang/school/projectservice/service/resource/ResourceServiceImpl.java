package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.FileDownloadException;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final ResourceMapper mapper;

    @Override
    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        log.debug("New storage size for project ID {}: {}", projectId, newStorageSize);

        checkIfStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());
        String folder = project.getId() + project.getName();

        log.debug("Uploading file to S3 folder: {}", folder);
        String key = s3Service.uploadFile(file, folder);
        Resource resource = createResource(key, file, teamMember, project);
        log.info("Resource created for file. Name: {}, Key: {}, Size: {}", resource.getName(), resource.getKey(), resource.getSize());

        resource = resourceRepository.save(resource);
        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("Resource added and storage size updated for project ID: {}", projectId);

        return mapper.toDto(resource);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId) {
        log.info("Deleting resource with ID: {}", resourceId);

        Resource resource = resourceRepository.findById(resourceId).orElseThrow(EntityNotFoundException::new);
        checkPermissions(userContext.getUserId(), resource);
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        s3Service.deleteFile(resource.getKey());
        log.debug("File with key {} deleted from S3", resource.getKey());

        Project project = resource.getProject();
        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize());
        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);
        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        log.info("Resource with ID {} deleted and project storage size updated", resourceId);
    }

    @Override
    public ResourceDownloadDto downloadResource(Long resourceId) {
        log.info("Downloading resource with ID: {}", resourceId);

        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new EntityNotFoundException("Resource not found with id: " + resourceId));
        byte[] fileBytes;
        String filename;
        try (InputStream fileStream = s3Service.downloadFile(resource.getKey())) {
            fileBytes = fileStream.readAllBytes();
            filename = getResourceName(resourceId);
        } catch (IOException e) {
            throw new FileDownloadException("Failed to download file for resource: " + resourceId, e);
        }
        log.info("File for resource ID {} successfully retrieved from S3", resourceId);

        return new ResourceDownloadDto(fileBytes, filename);
    }

    @Override
    public String getResourceName(Long resourceId) {
        log.info("Retrieving resource name for resource ID: {}", resourceId);
        String resourceName = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + resourceId))
                .getName();
        log.info("Resource name for ID {}: {}", resourceId, resourceName);

        return resourceName;
    }

    private void checkIfStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSize) {
        log.debug("Checking if new storage size {} exceeds maximum limit {}", newStorageSize, maxStorageSize);
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            log.error("Storage size exceeded: new size {} exceeds maximum {}", newStorageSize, maxStorageSize);
            throw new StorageSizeExceededException("Storage size exceeded the maximum limit");
        }
    }

    private void checkPermissions(Long teamMemberId, Resource resource) {
        log.debug("Checking permissions for team member ID {} to access resource ID {}", teamMemberId, resource.getId());
        if (!Objects.equals(teamMemberId, resource.getCreatedBy().getId())) {
            log.error("User with ID {} does not have permission to delete resource ID {}", teamMemberId, resource.getId());
            throw new SecurityException("User does not have permission to delete this file");
        }
    }

    private Resource createResource(String key, MultipartFile file, TeamMember teamMember, Project project) {
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);

        return resource;
    }
}
