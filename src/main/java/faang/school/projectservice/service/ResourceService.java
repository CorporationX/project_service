package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;

    @Value("${resource_service.folder}")
    private String folderEnd;

    @Transactional(readOnly = true)
    public InputStream downloadResourceFile(long resourceId) {
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public ResourceDto addResourceFile(Long projectId, MultipartFile file, long userId) {
        Project project = projectService.getProjectModelById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);

        String folder = project.getId() + project.getName() + folderEnd;
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMemberRepository.findById(userId));
        resource.setUpdatedBy(teamMemberRepository.findById(userId));
        project.setStorageSize(newStorageSize);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void deleteResourceFile(Long resourceId, long userId) {
        Resource resource = getResourceById(resourceId);
        s3Service.deleteFile(resource.getKey());

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(teamMemberRepository.findById(userId));

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
    }

    private void checkStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Storage size exceeded");
        }
    }

    private Resource getResourceById(long resourceId) {
        return resourceRepository.getReferenceById(resourceId);
    }
}
