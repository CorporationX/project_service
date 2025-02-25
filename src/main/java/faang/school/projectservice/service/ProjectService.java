package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ResourceValidator resourceValidator;
    private final ProjectValidator projectValidator;
    private final AmazonS3Service amazonS3Client;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceRepository resourceRepository;
    private final UserContext userContext;

    @Transactional
    public ResourceReadDto uploadResource(long projectId, long resourceId, MultipartFile file) {
        resourceValidator.validateResource(file);

        long userId = userContext.getUserId();
        Project project = getProject(projectId);
        String folder = projectId + project.getName();
        TeamMember user = getUserInProject(userId, projectId);
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize;

        if (resourceRepository.existsById(resourceId)) {
            Resource oldResource = project.getResources().stream()
                    .filter(resource -> resource.getId().equals(resourceId))
                    .findFirst()
                    .orElseThrow(() ->
                            new EntityNotFoundException("Изменение невозможно: такого файла нет в проекте"));
            newStorageSize = project.getStorageSize().subtract(oldResource.getSize()).add(fileSize);
        } else {
            newStorageSize = project.getStorageSize().add(fileSize);
        }

        projectValidator.validateProjectStorageSize(newStorageSize, project, fileSize);

        Resource uploadedResource = uploadResourceToStorage(file, folder);

        uploadedResource.setProject(project);

        if (uploadedResource.getType().equals(ResourceType.IMAGE)) {
            project.getGalleryFileKeys().add(uploadedResource.getKey());

        }

        project.getResources().add(uploadedResource);
        project.setStorageSize(newStorageSize);
        uploadedResource.setUpdatedBy(user);

        projectRepository.save(project);

        return resourceMapper.toDto(resourceRepository.save(uploadedResource));
    }

    public List<ResourceReadDto> getGallery(long projectId) {
        Project project = getProject(projectId);
        if (project.getGalleryFileKeys().isEmpty() || project.getResources().isEmpty()) {
            throw new DataValidationException("Галерея проекта пуста");
        }

        return project.getResources().stream().filter(image -> image.getType().equals(ResourceType.IMAGE)).map(resourceMapper::toDto).toList();
    }

    @Transactional
    public ResourceReadDto deleteResource(long projectId, long resourceId) {
        long userId = userContext.getUserId();
        Project project = getProject(projectId);
        Resource findingResource = project.getResources().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Удаление невозможно: такого файла нет в проекте"));
        TeamMember user = getUserInProject(userId, projectId);
        BigInteger storageSizeAfterDelete = project.getStorageSize().subtract(findingResource.getSize());

        if (!(project.getOwnerId().equals(userId)) && !(user.getRoles().contains(TeamRole.MANAGER))) {
            throw new AccessDeniedException("У вас нет доступа для удаления файла");
        }

        project.setStorageSize(storageSizeAfterDelete);

        if (findingResource.getType().equals(ResourceType.IMAGE)) {
            project.getGalleryFileKeys().remove(findingResource.getKey());

        }

        amazonS3Client.deleteFile(findingResource.getKey());
        Resource updatedResource = changeResourceStatusToDeleted(findingResource, user);
        projectRepository.save(project);

        return resourceMapper.toDto(updatedResource);
    }

    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Проект с id=" + id + " не найден"));
    }

    private Resource uploadResourceToStorage(MultipartFile file, String folder) {
        String uploadedResourceKey = amazonS3Client.uploadFile(file, folder);

        return Resource.builder()
                .key(uploadedResourceKey)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Resource changeResourceStatusToDeleted(Resource resource, TeamMember user) {
        resource.setKey("");
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(user);

        return resourceRepository.save(resource);
    }

    private TeamMember getUserInProject(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
