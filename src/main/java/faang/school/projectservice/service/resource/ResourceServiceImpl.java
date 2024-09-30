package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.exception.ForbiddenAccessException;
import faang.school.projectservice.exception.StorageSizeExceededException;
import faang.school.projectservice.exception.UserNotTeamMemberException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    private final static BigInteger BYTE_TO_MB_DELIMETER = new BigInteger("1048576");

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final UserContext userContext;
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    @Override
    public ResourceResponseObject getResourceById(Long resourceId) {
        long userId = userContext.getUserId();
        Resource resource = findResourceById(resourceId);
        if (teamMemberJpaRepository.existsByUserIdAndProjectId(userId, resource.getProject().getId())) {
            return s3Service.downloadFile(resource.getKey());
        }
        throw new ForbiddenAccessException("User with id %d cannot access to team's files".formatted(userId));
    }

    @Override
    public void deleteResourceById(Long resourceId) {
        long userId = userContext.getUserId();
        Resource resource = findResourceById(resourceId);
        Project project = resource.getProject();
        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, project.getId());
        if (teamMember.getRoles().contains(TeamRole.MANAGER) ||
        resource.getCreatedBy().getUserId().equals(userId)) {
            s3Service.deleteFile(resource.getKey());
            resource.setKey(null);
            BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize());
            resource.setSize(null);
            resource.setStatus(ResourceStatus.DELETED);
            resource.setUpdatedBy(teamMember);
            resourceRepository.save(resource);
            log.info("Resource with id {} deleted successfully", resourceId);
            updateProjectSize(project, newStorageSize);
        }else {
            throw new ForbiddenAccessException("User with id %d cannot delete resource".formatted(userId));
        }
    }

    @Override
    public ResourceDto updateResourceById(Long resourceId, MultipartFile file) {
        long userId = userContext.getUserId();
        Resource resource = findResourceById(resourceId);
        BigInteger resourceSize = resource.getSize();
        Project project = resource.getProject();
        BigInteger newStorageSize = project.getStorageSize()
                .subtract(resourceSize)
                .add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());
        s3Service.uploadFile(file, resource.getKey());
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setUpdatedBy(getTeamMemberByUserIdInProject(userId, resource.getProject().getId()));
        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource with id {} updated successfully.", updatedResource.getId());
        updateProjectSize(project, newStorageSize);
        return resourceMapper.toResourceDto(updatedResource);
    }

    @Override
    public ResourceDto addResourceToProject(Long projectId, MultipartFile file) {
        long userId = userContext.getUserId();
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String key = generateResourceKey(project, file.getOriginalFilename());
        s3Service.uploadFile(file, key);

        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, projectId);
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setName(file.getOriginalFilename());
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setAllowedRoles(new ArrayList<>(teamMember.getRoles()));
        resourceRepository.save(resource);
        log.info("resource '{}' for project with id {} saved successfully", resource.getKey(), projectId);
        updateProjectSize(project, newStorageSize);
        return resourceMapper.toResourceDto(resource);
    }

    private TeamMember getTeamMemberByUserIdInProject(long userId, long projectId) {
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            throw new ForbiddenAccessException("User with id %d is not team member of project with id %d".formatted(userId, projectId));
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
        BigInteger maxSizeInMB = maxSize.divide(BYTE_TO_MB_DELIMETER);
        if (newSize.compareTo(maxSize) > 0) {
            throw new StorageSizeExceededException("Out of space! You cannot upload this file due to the %dMBs limit".formatted(maxSizeInMB));
        }
    }

    private String generateResourceKey(Project project, String filename) {
        String folder = "%d-%s".formatted(project.getId(), project.getName());
        return String.format("%s/%d%s", folder, System.currentTimeMillis(), filename);
    }

    private void updateProjectSize(Project project, BigInteger newStorageSize) {
        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        log.info("Project size now equals {}", newStorageSize);
    }
}
