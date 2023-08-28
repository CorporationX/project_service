package faang.school.projectservice.service.project;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.GetResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.dto.resource.UpdateResourceDto;
import faang.school.projectservice.exception.FileUploadException;
import faang.school.projectservice.exception.InvalidCurrentUserException;
import faang.school.projectservice.exception.StorageSpaceExceededException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.resource.ResourceStatus;
import faang.school.projectservice.model.resource.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectFileService {
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final FileService fileService;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto uploadFile(MultipartFile multipartFile, long projectId, long userId) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = findTeamMember(project, userId);
        validateFreeStorageCapacity(project, BigInteger.valueOf(multipartFile.getSize()));

        String objectKey = fileService.upload(multipartFile, projectId);

        Resource resource = Resource.builder()
                .name(multipartFile.getOriginalFilename())
                .key(objectKey)
                .size(BigInteger.valueOf(multipartFile.getSize()))
                .type(ResourceType.getResourceType(multipartFile.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .project(project)
                .build();

        updateProjectStorage(resource);
        resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public UpdateResourceDto updateFile(MultipartFile multipartFile, long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        TeamMember updatedBy = teamMemberRepository.findById(userId);
        validateFileOnUpdate(resource.getName(), multipartFile.getOriginalFilename());
        validateIfUserCanChangeFile(resource, userId);
        validateStorageCapacityOnUpdate(resource, BigInteger.valueOf(multipartFile.getSize()));

        fileService.delete(resource.getKey());
        String key = fileService.upload(multipartFile, resource.getProject().getId());

        resource.setUpdatedBy(updatedBy);
        resource.setKey(key);
        resource.setUpdatedAt(LocalDateTime.now());

        updateProjectStorage(resource);
        resourceRepository.save(resource);

        return resourceMapper.toUpdateDto(resource);
    }

    @Transactional
    public void deleteFile(long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        validateIfUserCanChangeFile(resource, userId);

        if (!resource.getStatus().equals(ResourceStatus.DELETED)) {
            fileService.delete(resource.getKey());

            resource.setStatus(ResourceStatus.DELETED);
            updateProjectStorage(resource);
            resourceRepository.save(resource);
        }
    }

    public GetResourceDto getFile(long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        findTeamMember(resource.getProject(), userId);
        S3Object file = fileService.getFile(resource.getKey());

        return GetResourceDto.builder()
                .name(resource.getName())
                .type(file.getObjectMetadata().getContentType())
                .inputStream(file.getObjectContent())
                .size(resource.getSize().longValue())
                .build();
    }


    private TeamMember findTeamMember(Project project, long userId) {
        Optional<TeamMember> matchingMember = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getUserId() == userId)
                .findAny();

        if (matchingMember.isEmpty()) {
            String errorMessage = String.format(
                    "The user with id: %d is not on the project", userId);
            throw new InvalidCurrentUserException(errorMessage);
        } else {
            return matchingMember.get();
        }
    }

    private void validateFreeStorageCapacity(Project project, BigInteger fileSize) {
        if (fileSize.compareTo(project.getStorageSize()) > 0) {
            String errorMessage = String.format(
                    "Project %d storage has not enough space", project.getId());
            throw new StorageSpaceExceededException(errorMessage);
        }
    }

    private void validateStorageCapacityOnUpdate(Resource resource, BigInteger fileSize) {
        BigInteger storageSize = resource.getProject().getStorageSize();
        BigInteger resourceSize = resource.getSize();
        BigInteger storageCapacity = storageSize.add(resourceSize);

        if (fileSize.compareTo(storageCapacity) > 0) {
            String errorMessage = String.format(
                    "Impossible to update %s, project %d storage has not enough space",
                    resource.getName(), resource.getProject().getId());
            throw new StorageSpaceExceededException(errorMessage);
        }

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

    private void validateIfUserCanChangeFile(Resource resource, long userId) {
        boolean notAProjectManager = !userIsProjectManager(resource.getProject(), userId);
        boolean notAFileCreator = !userIsFileCreator(resource, userId);

        if (notAProjectManager && notAFileCreator) {
            throw new InvalidCurrentUserException(
                    "You should be creator of a file or a project manager to change files");
        }
    }

    private boolean userIsProjectManager(Project project, long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .anyMatch(teamMember ->
                        teamMember.getRoles().contains(TeamRole.MANAGER) && teamMember.getUserId().equals(userId));
    }

    private boolean userIsFileCreator(Resource resource, long userId) {
        return resource.getCreatedBy().getUserId().equals(userId);
    }

    private void validateFileOnUpdate(String resourceName, String fileOriginalName) {
        if (!resourceName.equals(fileOriginalName)) {
            throw new FileUploadException("File names don't match");
        }
    }
}
