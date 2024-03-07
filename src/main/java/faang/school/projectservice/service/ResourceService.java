package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
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
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMapper resourceMapper;

    public ResourceDto addResource(Long projectId, MultipartFile file, Long userId) {
        Project project = projectService.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);
        teamMemberIdExists(userId);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMemberRepository.findById(userId));
        resource.setUpdatedBy(teamMemberRepository.findById(userId));
        resource = resourceRepository.save(resource);

        saveProject(project, newStorageSize);

        return resourceMapper.toDto(resource);
    }

    public ResourceDto updateResource(Long resourceId, MultipartFile file, Long userId) {
        Resource currentResource = getResourceWithCheckedPermissions(resourceId, userId);
        Project project = currentResource.getProject();

        BigInteger newStorageSize = project.getStorageSize()
                .subtract(currentResource.getSize())
                .add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);

        String folder = project.getId() + project.getName();
        s3Service.deleteFile(currentResource.getKey());
        Resource resource = s3Service.uploadFile(file, folder);

        updateResourceFiles(currentResource, resource);
        saveProject(project, newStorageSize);

        return resourceMapper.toDto(currentResource);
    }

    @Transactional
    public void deleteResource(Long resourceId, Long userId) {
        Resource resource = getResourceWithCheckedPermissions(resourceId, userId);
        s3Service.deleteFile(resource.getKey());
        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(teamMemberRepository.findById(userId));
        resource.setKey(null);
        resource.setSize(null);
    }

    public InputStream downloadResource(Long resourceId) {
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    private void checkStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Storage size exceeded. Buy a subscription to increase the size of storage");
        }
    }

    private void teamMemberIdExists(Long userId) {
        if (teamMemberRepository.findById(userId) == null) {
            throw new IllegalArgumentException("There are no this user in team");
        }
    }

    private Resource getResourceWithCheckedPermissions(Long resourceId, Long userId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("There are no resource with this ID in DB"));

        if (!(resource.getCreatedBy().getId().equals(userId)) && !(resource.getCreatedBy().getRoles().contains(TeamRole.MANAGER))) {
            throw new IllegalArgumentException("Only author of resource and manager can update the file");
        }

        return resource;
    }

    private void updateResourceFiles(Resource resourceFromDB, Resource resource) {
        resourceFromDB.setKey(resource.getKey());
        resourceFromDB.setSize(resource.getSize());
        resourceFromDB.getUpdatedBy().setId(resource.getId());
        resourceFromDB.setUpdatedAt(resource.getUpdatedAt());
        resourceFromDB.setName(resource.getName());
        resourceFromDB.setType(resource.getType());
        resourceRepository.save(resourceFromDB);
    }

    private void saveProject(Project project, BigInteger newStorageSize) {
        project.setStorageSize(newStorageSize);
        projectService.save(project);
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("There are no resource with this ID"));
    }

}
