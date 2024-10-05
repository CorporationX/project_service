package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.resource.ResourceDto;
import faang.school.projectservice.dto.response.ResourceResponseObject;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenAccessException;
import faang.school.projectservice.exception.StorageSizeExceededException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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
        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, resource.getProject().getId());
        boolean hasAllowedRole = teamMember.getRoles()
                .stream()
                .anyMatch(role -> resource.getAllowedRoles().contains(role) || role.equals(TeamRole.MANAGER));
        if (!hasAllowedRole) {
            throw new ForbiddenAccessException(
                    "User with id %d cannot access to team files having roles: %s. Allowed roles: %s"
                            .formatted(userId, teamMember.getRoles(), resource.getAllowedRoles())
            );
        }
        return s3Service.downloadFile(resource.getKey());
    }

    @Override
    public void deleteResourceById(Long resourceId) {
        long userId = userContext.getUserId();
        Resource resource = findResourceById(resourceId);
        Project project = resource.getProject();
        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, project.getId());
        if (notCreatorOrManager(resource, teamMember)) {
            throw new ForbiddenAccessException("User with id %d cannot delete resource".formatted(userId));
        }
        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize());
        String key = resource.getKey();
        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);
        resourceRepository.save(resource);
        updateProjectSize(project, newStorageSize);
        s3Service.deleteFile(key);
        log.info("Resource with id {} deleted successfully", resourceId);
    }

    @Override
    public ResourceDto updateResourceById(Long resourceId, MultipartFile file) {
        long userId = userContext.getUserId();
        Resource resource = findResourceById(resourceId);
        BigInteger resourceSize = resource.getSize();
        Project project = resource.getProject();
        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, project.getId());
        if (notCreatorOrManager(resource, teamMember)) {
            throw new ForbiddenAccessException("User with id %d cannot update resource".formatted(userId));
        }
        BigInteger newStorageSize = project.getStorageSize()
                .subtract(resourceSize)
                .add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());
        Resource updatedResource = getUpdatedResource(resource, file);
        updatedResource.setCreatedBy(teamMember);
        resourceRepository.save(updatedResource);
        updateProjectSize(project, newStorageSize);
        s3Service.uploadFile(file, resource.getKey());
        log.info("Resource with id {} updated successfully.", updatedResource.getId());
        return resourceMapper.toResourceDto(updatedResource);
    }

    @Override
    public ResourceDto addResourceToProject(Long projectId, MultipartFile file) {
        long userId = userContext.getUserId();
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());
        String key = generateResourceKey(project, file.getOriginalFilename());
        TeamMember teamMember = getTeamMemberByUserIdInProject(userId, project.getId());
        Resource resource = Resource.builder()
                .name(file.getOriginalFilename())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .project(project)
                .key(key)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .build();
        resourceRepository.save(resource);
        updateProjectSize(project, newStorageSize);
        s3Service.uploadFile(file, resource.getKey());
        log.info("resource '{}' for project with id {} saved successfully", resource.getKey(), projectId);
        return resourceMapper.toResourceDto(resource);
    }

    private boolean notCreatorOrManager(Resource resource, TeamMember teamMember) {
        return !(resource.getCreatedBy().getUserId().equals(teamMember.getUserId()) || teamMember.getRoles().contains(TeamRole.MANAGER));
    }

    private Resource getUpdatedResource(Resource resource, MultipartFile file) {
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        return resource;
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
