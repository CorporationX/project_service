package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final UserServiceClient userServiceClient;
    private final ResourceMapper resourceMapper;
    private final S3Service s3Service;

    public ResourceDto uploadResource(long projectId, long userId, MultipartFile file) {
        log.info("Starting uploadResource for projectId={} and userId={}", projectId, userId);
        Project project = projectService.findProjectById(projectId);
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(fileSize);
        String type = file.getContentType();
        String originalName = file.getOriginalFilename();

        validateUserExists(userId);
        validateUserIsTeamMember(userId, projectId);
        validateStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        TeamMember creatorMember = teamMemberService.getProjectMember(userId, projectId);

        String key = s3Service.uploadFile(file, folder);

        Resource resource = Resource.builder()
                .name(originalName)
                .key(key)
                .size(fileSize)
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(type))
                .project(project)
                .createdBy(creatorMember)
                .updatedBy(creatorMember)
                .allowedRoles(new ArrayList<>(creatorMember.getRoles()))
                .build();

        resource = resourceRepository.save(resource);
        projectService.updateStorageSize(newStorageSize, project);

        log.info("File={} uploaded successfully for projectId={}", resource, projectId);
        return resourceMapper.toResourceDto(resource);
    }

    public ResourceDto deleteResource(long recourseId, long userId) {
        log.info("Starting deleteResource for recourseId={} and userId={}", recourseId, userId);
        Resource resource = getResourceById(recourseId);
        Project project = resource.getProject();
        TeamMember requestingMember = teamMemberService.getProjectMember(userId, project.getId());

        validateUserExists(userId);
        validateUserIsTeamMember(userId, project.getId());
        validateMemberCanDelete(requestingMember, resource);

        s3Service.deleteResource(resource.getKey());

        BigInteger newProjectStorageSize = project.getStorageSize().subtract(resource.getSize());
        projectService.updateStorageSize(newProjectStorageSize, project);

        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(requestingMember);
        resource = resourceRepository.save(resource);

        log.info("File={} deleted successfully in projectId={}", resource, project.getId());
        return resourceMapper.toResourceDto(resource);
    }

    public ResourceDto updateResource(long recourseId, long userId, MultipartFile file) {
        log.info("Starting updateResource with file={} for recourseId={} and userId={}", file, recourseId, userId);
        Resource oldResource = getResourceById(recourseId);
        Project project = oldResource.getProject();
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().subtract(oldResource.getSize()).add(fileSize);
        String type = file.getContentType();
        String originalName = file.getOriginalFilename();

        validateUserExists(userId);
        validateUserIsTeamMember(userId, project.getId());
        validateStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        TeamMember updaterMember = teamMemberService.getProjectMember(userId, project.getId());

        log.debug("Deleting old file with key={} from S3", oldResource.getKey());
        s3Service.deleteResource(oldResource.getKey());
        projectService.updateStorageSize(newStorageSize, project);

        String key = s3Service.uploadFile(file, folder);

        Resource updatedResource = Resource.builder()
                .name(originalName)
                .key(key)
                .size(fileSize)
                .status(ResourceStatus.ACTIVE)
                .type(ResourceType.getResourceType(type))
                .build();

        resourceMapper.updateOldResourceWithUpdateDataResource(updatedResource, oldResource);
        oldResource.setUpdatedBy(updaterMember);
        oldResource.setUpdatedAt(LocalDateTime.now());
        oldResource.getAllowedRoles().addAll(updaterMember.getRoles());

        Resource savedResource = resourceRepository.save(oldResource);
        log.info("File={} updated successfully in projectId={}", savedResource, project.getId());
        return resourceMapper.toResourceDto(savedResource);
    }

    public Resource getResourceById(long resourceId) {
        log.debug("searching recourse with id={}", resourceId);
        return resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Recourse not found by id=" + resourceId));
    }

    private void validateUserExists(long userId) {
        try {
            log.debug("Validating if user exists with userId={}", userId);
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            log.error("User not exist with userID={}", userId);
            throw new EntityNotFoundException("User not exist");
        }
    }

    private void validateUserIsTeamMember(long userId, long projectId) {
        if (teamMemberService.teamMemberInProjectNotExists(userId, projectId)) {
            log.error("User with userId={} is not a member of projectId={}", userId, projectId);
            throw new DataValidationException("don't upload, this user is not member of project");
        }
    }

    private void validateMemberCanDelete(TeamMember teamMember, Resource resource) {
        if (!teamMember.isManager() && resource.isNotCreator(teamMember)) {
            log.error("Member{} with roles={} don't delete file={}", teamMember, teamMember.getRoles(), resource);
            throw new DataValidationException("Manager or Creator can delete file");
        }
    }

    private void validateStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSizeProject) {
        if (newStorageSize.compareTo(maxStorageSizeProject) > 0) {
            log.error("the file size={} is larger than the project storage size={}"
                    , newStorageSize, maxStorageSizeProject);
            throw new DataValidationException("File size must be less or equal than size="
                    + maxStorageSizeProject + " project Storage");
        }
    }
}