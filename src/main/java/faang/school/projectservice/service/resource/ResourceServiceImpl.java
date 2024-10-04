package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService{

    private final ProjectService projectService;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final UserContext userContext;
    private final ResourceMapper mapper;

    @Override
    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        log.info("Adding resource to project with ID: {}", projectId);

        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        Project project = projectService.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        log.debug("New storage size for project ID {}: {}", projectId, newStorageSize);

        checkIfStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());
        String folder = project.getId() + project.getName();

        log.debug("Uploading file to S3 folder: {}", folder);
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource = resourceRepository.save(resource);
        project.setStorageSize(newStorageSize);
        projectService.save(project);

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
        projectService.save(project);
        log.info("Resource with ID {} deleted and project storage size updated", resourceId);
    }

    @Override
    public InputStream downloadResource(Long resourceId) {
        log.info("Downloading resource with ID: {}", resourceId);
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(EntityNotFoundException::new);
        InputStream fileStream = s3Service.downloadFile(resource.getKey());
        log.info("File for resource ID {} successfully retrieved from S3", resourceId);
        return fileStream;
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
}
