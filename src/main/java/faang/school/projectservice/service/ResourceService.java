package faang.school.projectservice.service;

import faang.school.projectservice.exceptions.FileStorageException;
import faang.school.projectservice.exceptions.ForbiddenException;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.exceptions.StorageLimitExceededException;
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

import java.io.IOException;
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
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new NotFoundException(PROJECT_NOT_FOUND));

        TeamMember currentMember = teamMemberRepository.findById(uploaderId).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND));

        if (!project.getMemberRoles().containsKey(currentMember)) {
            throw new ForbiddenException(USER_NOT_MEMBER);
        }

        BigInteger maxStorage = project.isHasExtendedStorage()
                ? SUBSCRIBE_MAX_STORAGE_BYTES
                : BASE_MAX_STORAGE_BYTES;

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        if (project.getStorageSize().add(fileSize).compareTo(maxStorage) > 0) {
            throw new StorageLimitExceededException(STORAGE_LIMIT_EXCEEDED);
        }

        if (!project.isHasExtendedStorage()) {
            BigInteger newSize = project.getStorageSize().add(fileSize);
            if (newSize.compareTo(BASE_MAX_STORAGE_BYTES) > 0) {
                throw new StorageLimitExceededException(STORAGE_LIMIT_EXCEEDED);
            }
        }

        String key = String.format("project-%d/%s", project.getId(), UUID.randomUUID());

        try {
            minioService.uploadFile(key, file);
        } catch (IOException e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new FileStorageException(UPLOAD_FAIL, e);
        }

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(fileSize);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(currentMember);
        resource.setUpdatedBy(currentMember);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setProject(project);
        resource.setAllowedRoles(new HashSet<>(currentMember.getRoles()));

        project.setStorageSize(project.getStorageSize().add(fileSize));
        projectRepository.save(project);

        return resourceRepository.save(resource);
    }

    @Transactional
    public void deleteFile(Long resourceId, Long userId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() ->
                new NotFoundException(RESOURCE_NOT_FOUND));

        TeamMember currentMember = teamMemberRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(USER_NOT_FOUND));

        Project project = resource.getProject();

        if (!project.getMemberRoles().containsKey(currentMember)) {
            throw new ForbiddenException(USER_NOT_MEMBER);
        }

        if (!resource.getCreatedBy().equals(currentMember) && !currentMember.hasRoleInProject(resource.getProject(),
                TeamRole.MANAGER)) {
            throw new ForbiddenException(NO_PERMISSION);
        }

        if (resource.getKey() != null) {
            minioService.deleteFile(resource.getKey());
        }

        BigInteger oldSize = resource.getSize();
        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(currentMember);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(resource);

        project.setStorageSize(project.getStorageSize().subtract(oldSize));
        projectRepository.save(project);
    }
}
