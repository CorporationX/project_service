package faang.school.projectservice.service.resources.implementations;

import faang.school.projectservice.config.project.ProjectStorageProperties;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.project.interfaces.ProjectService;
import faang.school.projectservice.service.resources.interfaces.ResourceService;
import faang.school.projectservice.service.s3.interfaces.S3Service;
import faang.school.projectservice.validation.project.ProjectValidator;
import faang.school.projectservice.validation.resource.ResourceValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ProjectStorageProperties projectStorageProperties;
    private final ProjectValidator projectValidator;
    private final ResourceValidator resourceValidator;

    @Transactional
    @Override
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectService.getProjectById(projectId);

        TeamMember member = projectValidator.validateProjectMembership(projectId);

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        checkStorageSizeExceeded(project, fileSize);

        String folder = projectStorageProperties.getFolderPrefix() + project.getId();
        Resource uploaded = s3Service.uploadFile(file, folder);

        Resource resource = populateResource(file, project, member, uploaded.getKey());
        resource = resourceRepository.save(resource);

        BigInteger newStorageSize = project.getStorageSize().add(fileSize);
        project.setStorageSize(newStorageSize);
        projectService.save(project);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    @Override
    public void deleteResource(Long projectId, Long resourceId) {
        Resource resource = resourceValidator.validateResourceAccess(projectId, resourceId);
        TeamMember member = projectValidator.validateProjectMembership(projectId);

        if (resource.getKey() != null) {
            s3Service.deleteFile(resource.getKey());
        }

        BigInteger oldSize = resource.getSize() != null ? resource.getSize() : BigInteger.ZERO;

        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(member);
        resource.setUpdatedBy(resource.getCreatedBy());
        resource.setUpdatedAt(LocalDateTime.now());

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(oldSize));
        projectService.save(project);
        resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ResourceDto> getResources(Long projectId) {
        projectValidator.validateProjectMembership(projectId);

        List<Resource> resources = resourceRepository
                .findAllByProjectIdAndStatus(projectId, ResourceStatus.ACTIVE);

        return resources.stream()
                .map(resourceMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ResourceDto updateResource(Long resourceId, Long projectId, MultipartFile file) {
        Resource resourceFromDB = resourceValidator.validateResourceAccess(projectId, resourceId);
        Project project = resourceFromDB.getProject();

        BigInteger oldSize = resourceFromDB.getSize() != null ? resourceFromDB.getSize() : BigInteger.ZERO;
        BigInteger newSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(newSize).subtract(oldSize);
        checkStorageSizeExceeded(project, newSize);

        if (resourceFromDB.getKey() != null) {
            s3Service.deleteFile(resourceFromDB.getKey());
        }

        String folder = projectStorageProperties.getFolderPrefix() + project.getId();
        Resource uploaded = s3Service.uploadFile(file, folder);

        TeamMember member = projectValidator.validateProjectMembership(projectId);

        resourceFromDB.setKey(uploaded.getKey());
        resourceFromDB.setSize(uploaded.getSize());
        resourceFromDB.setName(uploaded.getName());
        resourceFromDB.setType(uploaded.getType());
        resourceFromDB.setUpdatedBy(member);
        resourceFromDB.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(resourceFromDB);

        project.setStorageSize(newStorageSize);
        projectService.save(project);
        return resourceMapper.toDto(resourceFromDB);
    }


    private void checkStorageSizeExceeded(Project project, BigInteger fileSize) {
        BigInteger newSize = project.getStorageSize().add(fileSize);
        BigInteger maxSize = project.getMaxStorageSize() != null
                ? project.getMaxStorageSize()
                : projectStorageProperties.getDefaultMaxSize();

        if (newSize.compareTo(maxSize) > 0) {
            throw new StorageLimitExceededException(
                    ErrorMessage.STORAGE_LIMIT_EXCEEDED.getMessage(
                            project.getId(), maxSize.toString(), newSize.toString()
                    )
            );
        }
    }

    private Resource populateResource(MultipartFile file, Project project, TeamMember member, String key) {
        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(member)
                .updatedBy(member)
                .allowedRoles(member.getRoles())
                .project(project)
                .build();
    }
}
