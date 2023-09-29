package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.GetResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.UpdateResourceDto;
import faang.school.projectservice.exception.*;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.resource.ResourceStatus;
import faang.school.projectservice.model.resource.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.util.FileService;
import faang.school.projectservice.validator.ProjectResourceValidator;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class ProjectResourceService {

    private static final int MAX_ATTEMPTS = 5;

    private final ProjectRepository projectRepository;
    private final TeamMemberService teamMemberService;
    private final ResourceRepository resourceRepository;
    private final FileService fileService;
    private final ResourceMapper resourceMapper;
    private final ProjectResourceValidator projectResourceValidator;

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public ResourceDto uploadFile(MultipartFile multipartFile, long projectId, long userId) {
        Resource resource = uploadResource(multipartFile, projectId, userId);

        fileService.upload(multipartFile, resource.getKey());

        return resourceMapper.toDto(resource);
    }

    @Recover
    public ResourceDto recoverUpload(OptimisticLockException e,
                                     MultipartFile multipartFile,
                                     long projectId,
                                     long userId) {
        throw new FileUploadException(
                MessageFormat.format(
                        "Failed to upload the file {0} after {1} attempts due to concurrent modifications." +
                        "Please try again.", multipartFile.getOriginalFilename(), MAX_ATTEMPTS));
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public UpdateResourceDto updateFile(MultipartFile multipartFile, long resourceId, long userId) {
        Resource resource = updateResource(multipartFile, resourceId, userId);

        fileService.delete(resource.getKey());
        fileService.upload(multipartFile, resource.getKey());

        return resourceMapper.toUpdateDto(resource);
    }

    @Recover
    public UpdateResourceDto recoverUpdate(OptimisticLockException e,
                                           MultipartFile multipartFile,
                                           long resourceId,
                                           long userId) {
        throw new FileUpdateException(MessageFormat.format(
                "Failed to update the file {0} after {1} attempts due to concurrent modifications." +
                "Please try again.", multipartFile.getOriginalFilename(), MAX_ATTEMPTS));
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockException.class, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = 1000))
    public void deleteFile(long resourceId, long userId) {
        String key = deleteResource(resourceId, userId);

        fileService.delete(key);
    }

    @Recover
    public void recoverDelete(OptimisticLockException e,
                              long resourceId,
                              long userId) {
        throw new FileDeleteException(MessageFormat.format(
                "Failed to delete the file {0} after {1} attempts due to concurrent modifications." +
                "Please try again.", resourceId, MAX_ATTEMPTS));
    }

    @Transactional(readOnly = true)
    public GetResourceDto getFile(long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        teamMemberService.findByUserIdAndProjectId(userId, resource.getProject().getId());
        S3Object file = fileService.getFile(resource.getKey());

        return GetResourceDto.builder()
                .name(resource.getName())
                .type(file.getObjectMetadata().getContentType())
                .inputStream(file.getObjectContent())
                .size(resource.getSize().longValue())
                .build();
    }

    private Resource uploadResource(MultipartFile multipartFile, long projectId, long userId) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = teamMemberService.findByUserIdAndProjectId(userId, projectId);
        projectResourceValidator.validateFreeStorageCapacity(project, BigInteger.valueOf(multipartFile.getSize()));

        String fileKey = generateFileKey(multipartFile, projectId);
        Resource resource = fillUpResource(multipartFile, project, teamMember, fileKey);

        updateProjectStorage(resource);
        resourceRepository.save(resource);

        return resource;
    }

    private Resource updateResource(MultipartFile multipartFile, long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        TeamMember updatedBy = teamMemberService.findByUserIdAndProjectId(userId, resource.getProject().getId());

        projectResourceValidator.validateFileOnUpdate(resource.getName(), multipartFile.getOriginalFilename());
        projectResourceValidator.validateIfUserCanChangeFile(resource, userId);

        BigInteger setStorageCapacityOnUpdate = storageCapacityOnUpdate(
                resource, BigInteger.valueOf(multipartFile.getSize()));

        resource.getProject().setStorageSize(setStorageCapacityOnUpdate);
        resource.setUpdatedBy(updatedBy);
        resource.setKey(generateFileKey(multipartFile, resource.getProject().getId()));
        resource.setSize(BigInteger.valueOf(multipartFile.getSize()));

        resourceRepository.save(resource);
        updateProjectStorage(resource);

        return resource;
    }

    private String deleteResource(long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        TeamMember updatedBy = teamMemberService.findByUserIdAndProjectId(userId, resource.getProject().getId());

        projectResourceValidator.validateIfUserCanChangeFile(resource, userId);
        projectResourceValidator.validateResourceOnDelete(resource);

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(updatedBy);
        updateProjectStorage(resource);
        resourceRepository.save(resource);

        return resource.getKey();
    }

    private String generateFileKey(MultipartFile multipartFile, long projectId) {
        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();

        return String.format("p%d_%s_%s", projectId, size, fileName);
    }

    private BigInteger storageCapacityOnUpdate(Resource resource, BigInteger fileSize) {
        BigInteger storageSize = resource.getProject().getStorageSize();
        BigInteger resourceSize = resource.getSize();
        BigInteger storageCapacity = storageSize.add(resourceSize);

        if (fileSize.compareTo(storageCapacity) > 0) {
            String errorMessage = String.format(
                    "Impossible to update %s, project %d storage has not enough space",
                    resource.getName(), resource.getProject().getId());
            throw new StorageSpaceExceededException(errorMessage);
        }

        return storageCapacity;
    }

    private void updateProjectStorage(Resource resource) {
        Project project = resource.getProject();
        BigInteger storageSize = project.getStorageSize();
        BigInteger resourceSize = resource.getSize();

        if (resource.getStatus().equals(ResourceStatus.DELETED)) {
            project.setStorageSize(storageSize.add(resourceSize));
        } else {
            project.setStorageSize(storageSize.subtract(resourceSize));
        }

        projectRepository.save(project);
    }

    private Resource fillUpResource(MultipartFile multipartFile,
                                    Project project,
                                    TeamMember teamMember,
                                    String fileKey) {
        return Resource.builder()
                .name(multipartFile.getOriginalFilename())
                .key(fileKey)
                .size(BigInteger.valueOf(multipartFile.getSize()))
                .type(ResourceType.getResourceType(multipartFile.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .project(project)
                .build();
    }
}
