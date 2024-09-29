package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.exception.UserNotTeamMemberException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final UserContext userContext;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    @Override
    public ResourceResponseObject getResourceById(Long resourceId) {
        Resource resource = findResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    @Override
    public void deleteResourceById(Long resourceId) {
        Resource resource = findResourceById(resourceId);
        s3Service.deleteFile(resource.getKey());
        resourceRepository.delete(resource);
        log.info("Resource with id {} deleted successfully", resourceId);
    }

    @Override
    public ResourceDto updateResourceById(Long resourceId, MultipartFile file) {
        long userId = userContext.getUserId();
        Resource resource = findResourceById(resourceId);
        s3Service.updateFile(file, resource.getKey());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(getTeamMemberByUserIdInProject(userId, resource.getProject().getId()));
        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource with id {} updated successfully.", updatedResource.getId());
        return resourceMapper.toResourceDto(updatedResource);
    }

    @Override
    public ResourceDto addResourceToProject(Long projectId, MultipartFile file) {
        long userId = userContext.getUserId();
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = "%d-%s".formatted(projectId, project.getName());
        Resource uploadedResource = s3Service.uploadFile(file, folder);
        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, projectId);
        uploadedResource.setProject(project);
        uploadedResource.setCreatedBy(teamMember);
        uploadedResource.setUpdatedBy(teamMember);
        uploadedResource.setAllowedRoles(new ArrayList<>(teamMember.getRoles()));
        resourceRepository.save(uploadedResource);
        log.info("resource '{}' for project with id {} saved successfully", uploadedResource.getName(), projectId);

        return resourceMapper.toResourceDto(uploadedResource);
    }

    private TeamMember getTeamMemberByUserIdInProject(long userId, long projectId) {
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            throw new UserNotTeamMemberException("User with id %d is not team member of project with id %d".formatted(userId, projectId));
        }
        log.info("team member with id {} of project with id {} found", teamMember.getId(),projectId);
        return teamMember;
    }

    private Resource findResourceById(Long resourceId) {
        return resourceRepository
                .findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with id %d not found".formatted(resourceId)));
    }

    private void checkStorageSizeExceeded(BigInteger newSize, BigInteger maxSize) {
        BigInteger maxSizeInMBs = maxSize.divide(new BigInteger("1048576"));
        if (newSize.compareTo(maxSize) > 0) {
            throw new StorageSizeExceededException("Out of space! You cannot upload this file due to the %dMBs limit".formatted(maxSizeInMBs));
        }
    }
}
