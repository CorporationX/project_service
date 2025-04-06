package faang.school.projectservice.service.projectresource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceFileDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ResourceHandlingException;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.service.tika.TikaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectResourceServiceImpl implements ProjectResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final TikaService tikaService;

    @Value("${amazonS3.bucket-name}")
    private String bucketName;

    @Value("${project.storage.max-size}")
    private Long maxStorageSize;

    @Override
    @Async("fileUploadTaskExecutor")
    public ResourceFileDto uploadFile(Long projectId, MultipartFile file) throws IOException {
        Project project = projectService.getProjectById(projectId);
        TeamMember currentMember = teamMemberService.getCurrentTeamMember(projectId);
        validateStorageLimit(project, file.getSize());
        String key = generateFileKey(projectId, file.getOriginalFilename());
        s3Service.uploadFile(file, key);
        Resource resource = buildResource(file, key, project, currentMember);
        resourceRepository.save(resource);
        updateProjectStorage(project, file.getSize());
        return resourceMapper.toDto(resource);
    }


    @Override
    @Async("fileUploadTaskExecutor")
    public InputStream downloadFile(Long resourceId) throws ResourceHandlingException {
        Resource resource = findResourceById(resourceId);
        validateResourceStatus(resource);
        S3Object s3Object = s3Service.downloadFile(resource.getKey());
        return s3Object.getObjectContent();
    }

    @Override
    @Async("fileUploadTaskExecutor")
    public void deleteFile(Long resourceId) throws AccessDeniedException, ResourceHandlingException {
        Resource resource = findResourceById(resourceId);
        TeamMember currentMember = teamMemberService.getCurrentTeamMember(resource.getProject().getId());
        validatePermission(currentMember, resource);
        try {
            s3Service.deleteFile(resource.getKey());
            resourceRepository.delete(resource);
        } catch (Exception e) {
            log.error("Failed to delete resource {}", resourceId, e);
            throw new ResourceHandlingException("Failed to delete resource");
        }
        updateProjectStorage(resource.getProject(), -resource.getSize().longValue());
    }

    @Override
    public ResourceFileDto getResourceInfo(Long resourceId) throws ResourceHandlingException {
        Resource resource = findResourceById(resourceId);
        return resourceMapper.toDto(resource);
    }

    private void validateStorageLimit(Project project, long fileSize) {
        BigInteger newSize = project.getStorageSize().add(BigInteger.valueOf(fileSize));
        if (newSize.compareTo(BigInteger.valueOf(maxStorageSize)) > 0) {
            throw new StorageSizeExceededException("Storage limit exceeded");
        }
    }

    private void updateProjectStorage(Project project, long fileSize) {
        BigInteger newSize = project.getStorageSize().add(BigInteger.valueOf(fileSize));
        project.setStorageSize(newSize);
        projectRepository.save(project);
    }

    private void validatePermission(TeamMember member, Resource resource) {
        boolean isCreator = member.getId().equals(resource.getCreatedBy().getId());
        boolean isManager = member.getRoles().contains(TeamRole.MANAGER);
        if (!isCreator && !isManager) {
            throw new DataValidationException("No permission to modify resource");
        }
    }

    private void validateResourceStatus(Resource resource) {
        if (resource.getStatus() != ResourceStatus.ACTIVE) {
            throw new DataValidationException("Resource is not active");
        }
    }

    private Resource findResourceById(Long resourceId) throws ResourceHandlingException {
        return resourceRepository
                .findById(resourceId)
                .orElseThrow(() -> new ResourceHandlingException("Resource with id %d not found".formatted(resourceId)));
    }

    private String generateFileKey(Long projectId, String fileName) {
        return String.format("project-%d/%s-%s", projectId, UUID.randomUUID(), fileName);
    }

    private Resource buildResource(MultipartFile file, String key, Project project, TeamMember member) {
        String fileName = file.getOriginalFilename();
        String contentType = tikaService.detectMimeType(file);
        ResourceType resourceType = ResourceType.getResourceType(contentType);
        List<TeamRole> allowedRoles = new ArrayList<>(member.getRoles());
        return Resource.builder()
                .key(key)
                .name(fileName)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(allowedRoles)
                .type(resourceType)
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .createdBy(member)
                .updatedBy(member)
                .build();
    }
}
