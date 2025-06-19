package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.S3FileDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.common.RecordNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3AsyncService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.util.S3FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static faang.school.projectservice.validation.StorageValidation.projectResourcesAccessPermissionCheck;
import static faang.school.projectservice.validation.StorageValidation.storageFileAccessPermissionCheck;
import static faang.school.projectservice.validation.StorageValidation.storageSizeNotExceededValidation;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final S3Service s3Service;
    private final S3AsyncService s3AsyncService;
    private final S3FileUtil s3FileUtil;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public Resource getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RecordNotFoundException("There is no resource with id [%d]".formatted(resourceId)));

        if (Objects.equals(resource.getStatus(), ResourceStatus.DELETED)) {
            throw new DataValidationException("Resource with id [%d] was deleted".formatted(resourceId));
        }

        return resource;
    }

    @Transactional(readOnly = true)
    public Resource getResourceByKey(String key) {
        Resource resource = resourceRepository.findByKey(key)
                .orElseThrow(() -> new RecordNotFoundException("There is no resource with key [%s]".formatted(key)));

        if (Objects.equals(resource.getStatus(), ResourceStatus.DELETED)) {
            throw new DataValidationException("Resource with key [%s] was deleted".formatted(key));
        }

        return resource;
    }

    @Transactional(readOnly = true)
    public Resource getResourceByProjectIdAndKey(Long projectId, String key) {
        return resourceRepository.findByProjectIdAndKey(projectId, key)
                .orElseThrow(() -> new RecordNotFoundException("Resource for project [%d] with key [%s] not found".formatted(projectId, key)));
    }

    @Transactional
    public Resource addResource(Long projectId, MultipartFile file) {
        Project project = getValidProject(projectId);
        String folder = project.getId() + project.getName();
        TeamMember member = getValidTeamMember(userContext.getUserId(), projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));

        storageSizeNotExceededValidation(newStorageSize, project.getMaxStorageSize());
        projectResourcesAccessPermissionCheck(project, member);

        String key = s3Service.uploadFile(folder, file);

        Resource resource = Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .createdBy(member)
                .updatedBy(member)
                .project(project)
                .allowedRoles(getAllowedRoles(member))
                .build();

        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resource;
    }

    @Transactional
    public Resource uploadResourceAsync(Project project, MultipartFile file) {

        String key = s3FileUtil.getKey(project, file);
        TeamMember member = getValidTeamMember(userContext.getUserId(), project.getId());

        projectResourcesAccessPermissionCheck(project, member);

        s3AsyncService.uploadFileAsync(key, file, () -> markFileAsUploaded(project.getId(), key));

        Resource resource = Resource.builder()
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .status(ResourceStatus.INACTIVE)
                .type(ResourceType.getResourceType(file.getContentType()))
                .name(file.getOriginalFilename())
                .createdBy(member)
                .updatedBy(member)
                .project(project)
                .allowedRoles(getAllowedRoles(member))
                .build();

        return resourceRepository.save(resource);
    }

    @Transactional
    public Boolean deleteResource(Long projectId, Long resourceId) {
        Project project = getValidProject(projectId);
        Resource resource = getResourceById(resourceId);
        TeamMember member = getValidTeamMember(userContext.getUserId(), projectId);

        storageFileAccessPermissionCheck(resource, member);
        projectResourcesAccessPermissionCheck(project, member);

        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        s3Service.deleteFile(resource.getKey());

        resource.setKey(project.getId() + project.getName());
        resource.setSize(BigInteger.valueOf(0));
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(member);

        resourceRepository.save(resource);
        projectRepository.save(project);

        return true;
    }

    @Transactional(readOnly = true)
    public S3FileDto downloadFile(Long projectId, Long resourceId) {
        Project project = getValidProject(projectId);
        Resource resource = getResourceById(resourceId);
        TeamMember member = getValidTeamMember(userContext.getUserId(), projectId);

        projectResourcesAccessPermissionCheck(project, member);

        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public Resource updateResource(Long projectId, Long resourceId, MultipartFile file) {
        Project project = getValidProject(projectId);
        TeamMember member = getValidTeamMember(userContext.getUserId(), projectId);
        Resource resource = getResourceById(resourceId);

        projectResourcesAccessPermissionCheck(project, member);
        storageFileAccessPermissionCheck(resource, member);

        BigInteger sizeDifference = BigInteger.valueOf(file.getSize()).subtract(resource.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(sizeDifference);

        storageSizeNotExceededValidation(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resource.getKey());

        String key = s3Service.uploadFile(folder, file);
        resource.setKey(key);
        resource.setUpdatedBy(member);

        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resource;
    }

    private Project getValidProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new DataValidationException("There is no project with such id"));
    }

    private TeamMember getValidTeamMember(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }

    private List<TeamRole> getAllowedRoles(TeamMember member) {
        Set<TeamRole> updatedSet = new HashSet<>();
        updatedSet.addAll(member.getRoles());
        updatedSet.addAll(List.of(TeamRole.OWNER, TeamRole.MANAGER));
        return new ArrayList<>(updatedSet);
    }

    private void markFileAsUploaded(Long projectId, String key) {
        Resource uploadingFile = getResourceByProjectIdAndKey(projectId, key);
        uploadingFile.setStatus(ResourceStatus.ACTIVE);
        resourceRepository.save(uploadingFile);
    }
}
