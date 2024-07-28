package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceResponseDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import faang.school.projectservice.service.utilservice.ResourceUtilService;
import faang.school.projectservice.service.utilservice.TeamMemberUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceUtilService resourceUtilService;
    private final ResourceMapper resourceMapper;

    private final ProjectUtilService projectUtilService;
    private final TeamMemberUtilService teamMemberUtilService;

    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public ResourceResponseDto getByIdAndProjectId(long resourceId, long projectId) {

        Resource resource = resourceUtilService.getByIdAndProjectId(resourceId, projectId);
        return resourceMapper.toResponseDto(resource);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponseDto> getAllByProjectId(long projectId) {

        List<Resource> resources = resourceUtilService.getAllByProjectId(projectId);
        return resourceMapper.toResponseDtoList(resources);
    }

    public ResourceResponseDto uploadNew(MultipartFile multipartFile, long projectId, long userId) {

        Project project = projectUtilService.getById(projectId);
        TeamMember creator = teamMemberUtilService.getByUserIdAndProjectId(userId, projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(multipartFile.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + "_" + project.getName();
        String key = s3Service.uploadFile(multipartFile, folder);

        Resource resource = Resource.builder()
                .name(multipartFile.getName())
                .key(key)
                .size(BigInteger.valueOf(multipartFile.getSize()))
                .allowedRoles(creator.getRoles().stream().toList())
                .type(ResourceType.getResourceType(multipartFile.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(creator)
                .updatedBy(creator)
                .project(project)
                .build();
        resource = resourceUtilService.save(resource);

        project.setStorageSize(newStorageSize);
        projectUtilService.save(project);

        return resourceMapper.toResponseDto(resource);
    }

    public ResourceResponseDto delete(long resourceId, long projectId, long userId) {

        Resource resource = resourceUtilService.getByIdAndProjectId(resourceId, projectId);
        if (resource.getStatus().equals(ResourceStatus.DELETED)) {
            throw new ConflictException(String.format("Resource id=%d is already deleted", resourceId));
        }
        TeamMember updater = teamMemberUtilService.getByUserIdAndProjectId(userId, projectId);
        if (resource.getCreatedBy().getId() != userId) {
            Set<TeamRole> requesterRoles = Set.copyOf(updater.getRoles());
            if (!requesterRoles.contains(TeamRole.MANAGER)) {
                throw new AccessDeniedException(String.format(
                        "Current user id=%d dont have rights to delete resource", userId));
            }
        }

        BigInteger oldSize = resource.getSize();

        s3Service.deleteFile(resource.getKey());

        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(updater);

        resource = resourceUtilService.save(resource);

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(oldSize));
        projectUtilService.save(project);

        return resourceMapper.toResponseDto(resource);
    }

    private void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSize) {

        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new ConflictException(String.format(
                    "Storage size was exceeded (%d/%d)", newStorageSize, maxStorageSize));
        }
    }
}
