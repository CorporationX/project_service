package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final S3ServiceImpl s3ServiceImpl;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectService.getProjectById(projectId);
        BigInteger storageSize = Optional.ofNullable(project.getStorageSize()).orElse(BigInteger.ZERO);
        BigInteger newStorageSize = storageSize.add(BigInteger.valueOf(file.getSize()));
        resourceValidator.checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        Resource resource = s3ServiceImpl.uploadFile(file, project);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectService.save(project);
        resourceRepository.save(resource);
        return resourceMapper.toDto(resource);
    }

    public InputStream downloadResource(Long resourceId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        return s3ServiceImpl.downloadFile(resource.getKey());
    }

    @Transactional
    public void deleteResource(Long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        Project project = resource.getProject();
        resourceValidator.checkingAccessRights(userId, resource, project);

        String resourceKey = resource.getKey();
        s3ServiceImpl.deleteFile(resourceKey);

        subtractCapacity(project, resource);
        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(null);
    }

    private void subtractCapacity(Project project, Resource resource) {
        BigInteger newSizeCapacity = project.getStorageSize().subtract(resource.getSize());
        if (newSizeCapacity.compareTo(BigInteger.ZERO) < 0) {
            newSizeCapacity = BigInteger.ZERO;
        }
        project.setStorageSize(newSizeCapacity);
        projectService.save(project);
    }
}