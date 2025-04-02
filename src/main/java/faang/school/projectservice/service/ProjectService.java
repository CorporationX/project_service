package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.event.ProjectEvent;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.publisher.ProjectEventPublisher;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.imageprocessing.ImageProcessingUtils;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.service.upload.UploadData;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.project.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ResourceValidator resourceValidator;
    private final ProjectValidator projectValidator;
    private final AmazonS3Service amazonS3Client;
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final ProjectMapper projectMapper;
    private final ImageProcessingUtils imageProcessingUtils;
    private final ProjectEventPublisher projectEventPublisher;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto){
        if (projectRepository.existsByOwnerIdAndName(userContext.getUserId(), projectDto.getName())){
            throw new BusinessException("У пользователя не могут быть проекты с одинаковым названием");
        }

        Project project = projectMapper.toEntity(projectDto);
        project.setStatus(ProjectStatus.CREATED);
        project.setVisibility(projectDto.getProjectVisibility());
        project.setOwnerId(userContext.getUserId());

        project = projectRepository.save(project);
        projectEventPublisher.publish(new ProjectEvent(project.getOwnerId(), project.getId()));

        return projectMapper.toProjectDto(project);
    }

    public Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Проект с ID %d не найден", projectId)
                ));
    }

    @Transactional
    public ProjectCoverDto addProjectCover(Long projectId, MultipartFile file) {
        Project project = getProject(projectId);

        resourceValidator.validateResource(file);
        resourceValidator.checkFileSize(file.getSize());
        resourceValidator.checkIsFileImage(file);

        MultipartFile resizedImageBytes = imageProcessingUtils.convertByteToMultipartFile(
                imageProcessingUtils.resizeImage(file),
                file.getName(),
                file.getContentType()
        );

        String folder = String.format("%d_%s", project.getId(), project.getName());
        String key = amazonS3Client.uploadFile(resizedImageBytes, folder);
        project.setCoverImageId(key);
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectCoverDto(updatedProject);
    }

    @Transactional
    public ProjectCoverDto deleteProjectCover(Long projectId) {
        Project project = getProject(projectId);
        amazonS3Client.deleteFile(project.getCoverImageId());
        project.setCoverImageId(null);
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectCoverDto(updatedProject);
    }

    @Transactional
    public ResourceReadDto createResource(long projectId, MultipartFile file) {
        resourceValidator.validateResource(file);

        UploadData data = prepareUploadData(projectId, file);
        BigInteger newStorageSize = data.project().getStorageSize().add(data.fileSize());

        return uploadResource(data.project(), data.user(), file, data.fileSize(), data.folder(), newStorageSize);
    }

    @Transactional
    public ResourceReadDto editResource(long projectId, long resourceId, MultipartFile file) {
        resourceValidator.validateResource(file);

        UploadData data = prepareUploadData(projectId, file);
        Resource oldResource = data.project().getResources().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Изменение невозможно: такого файла нет в проекте"));
        BigInteger newStorageSize = data.project().getStorageSize().subtract(oldResource.getSize()).add(data.fileSize());

        return uploadResource(data.project(), data.user(), file, data.fileSize(), data.folder(), newStorageSize);
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
        TeamMember user = getUserFromProject(userId, projectId);
        validateUserHasAccess(project, user, userId);

        Resource findingResource = project.getResources().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Удаление невозможно: такого файла нет в проекте"));
        BigInteger storageSizeAfterDelete = project.getStorageSize().subtract(findingResource.getSize());

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

    private UploadData prepareUploadData(long projectId, MultipartFile file) {
        resourceValidator.validateResource(file);

        long userId = userContext.getUserId();
        Project project = getProject(projectId);
        String folder = projectId + project.getName();
        TeamMember user = getUserFromProject(userId, projectId);
        BigInteger fileSize = BigInteger.valueOf(file.getSize());

        return new UploadData(project, user, fileSize, folder);
    }

    private void validateUserHasAccess(Project project, TeamMember user, long userId) {
        if (!(project.getOwnerId().equals(userId)) && !(user.getRoles().contains(TeamRole.MANAGER))) {
            throw new AccessDeniedException("У вас нет доступа для удаления файла");
        }
    }

    private ResourceReadDto uploadResource(Project project, TeamMember user, MultipartFile file, BigInteger fileSize, String folder, BigInteger storageSize) {
        projectValidator.validateProjectStorageSize(storageSize, project, fileSize);

        Resource uploadedResource = uploadResourceToStorage(file, folder);

        uploadedResource.setUpdatedBy(user);
        uploadedResource.setProject(project);

        if (ResourceType.IMAGE.equals(uploadedResource.getType())) {
            project.getGalleryFileKeys().add(uploadedResource.getKey());
        }

        project.getResources().add(uploadedResource);
        project.setStorageSize(storageSize);

        projectRepository.save(project);

        return resourceMapper.toDto(resourceRepository.save(uploadedResource));
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
        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(user);

        return resourceRepository.save(resource);
    }

    private TeamMember getUserFromProject(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID %d в проекте %d не найден", userId, projectId)));
    }
}
