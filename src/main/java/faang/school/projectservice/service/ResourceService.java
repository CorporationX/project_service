package faang.school.projectservice.service;

import faang.school.projectservice.exception.AccessToDeniedException;
import faang.school.projectservice.exception.FileStorageException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import static faang.school.projectservice.constants.Constants.BASE_MAX_STORAGE_BYTES;
import static faang.school.projectservice.constants.Constants.NO_PERMISSION;
import static faang.school.projectservice.constants.Constants.PROJECT_NOT_FOUND;
import static faang.school.projectservice.constants.Constants.RESOURCE_NOT_FOUND;
import static faang.school.projectservice.constants.Constants.STORAGE_LIMIT_EXCEEDED;
import static faang.school.projectservice.constants.Constants.SUBSCRIBE_MAX_STORAGE_BYTES;
import static faang.school.projectservice.constants.Constants.UPLOAD_FAIL;
import static faang.school.projectservice.constants.Constants.USER_NOT_FOUND;
import static faang.school.projectservice.constants.Constants.USER_NOT_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MinioService minioService;

    @Transactional
    public Resource uploadFile(MultipartFile file, Long uploaderId, Long projectId) {
        Project project = getProjectById(projectId);
        TeamMember currentMember = getTeamMemberById(uploaderId);

        validateMemberInProject(project, currentMember);
        validateStorageLimit(project, file);

        String fileKey = uploadToMinio(file, project.getId());
        Resource resource = buildResource(file, currentMember, project, fileKey);

        updateProjectStorage(project, resource.getSize());

        return resourceRepository.save(resource);
    }

    @Transactional
    public void deleteFile(Long resourceId, Long userId) {
        Resource resource = getResourceById(resourceId);
        TeamMember currentMember = getTeamMemberById(userId);
        Project project = resource.getProject();

        validateMemberInProject(project, currentMember);
        validateDeletePermission(resource, currentMember);

        deleteFromMinio(resource.getKey());
        updateResourceForDeletion(resource, currentMember);
        updateProjectStorageAfterDeletion(project, resource.getSize());
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND));
    }

    private TeamMember getTeamMemberById(Long memberId) {
        return teamMemberRepository.findById(memberId).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND));
    }

    private void validateMemberInProject(Project project, TeamMember member) {
        if (!project.getMemberRoles().containsKey(member)) {
            throw new AccessToDeniedException(USER_NOT_MEMBER);
        }
    }

    private void validateStorageLimit(Project project, MultipartFile file) {
        BigInteger maxStorage = project.isHasExtendedStorage()
                ? SUBSCRIBE_MAX_STORAGE_BYTES
                : BASE_MAX_STORAGE_BYTES;

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newSize = project.getStorageSize().add(fileSize);

        if (newSize.compareTo(maxStorage) > 0) {
            throw new StorageLimitExceededException(STORAGE_LIMIT_EXCEEDED);
        }
    }

    private String uploadToMinio(MultipartFile file, Long projectId) {
        String key = String.format("project-%d/%s", projectId, UUID.randomUUID());

        try {
            minioService.uploadFile(key, file);
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new FileStorageException(UPLOAD_FAIL, e);
        }

        return key;
    }

    private Resource buildResource(MultipartFile file, TeamMember member, Project project, String key) {
        return Resource.builder().key(key).size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType())).status(ResourceStatus.ACTIVE)
                .createdBy(member).updatedBy(member).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .project(project).allowedRoles(new HashSet<>(member.getRoles())).build();
    }

    private void updateProjectStorage(Project project, BigInteger size) {
        project.setStorageSize(project.getStorageSize().add(size));
        projectRepository.save(project);
    }

    private void validateDeletePermission(Resource resource, TeamMember member) {
        boolean isCreator = resource.getCreatedBy().equals(member);
        boolean isManager = member.hasRoleInProject(resource.getProject(), TeamRole.MANAGER);

        if (!isCreator && !isManager) {
            throw new AccessToDeniedException(NO_PERMISSION);
        }
    }

    private void deleteFromMinio(String key) {
        if (key != null) {
            minioService.deleteFile(key);
        }
    }

    private void updateResourceForDeletion(Resource resource, TeamMember member) {
        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(member);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(resource);
    }

    private void updateProjectStorageAfterDeletion(Project project, BigInteger oldSize) {
        project.setStorageSize(project.getStorageSize().subtract(oldSize));
        projectRepository.save(project);
    }
}
