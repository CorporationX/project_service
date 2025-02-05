package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
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
    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceReadDto uploadResourceToGallery(long projectId, MultipartFile file) {
        resourceValidator.validateResource(file);

        Project project = getProject(projectId);
        String folder = projectId + project.getName();
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(fileSize);

        projectValidator.validateProjectStorageSize(newStorageSize, project, fileSize);

        Resource uploadedResource = uploadResourceToStorage(file, folder);

        uploadedResource.setProject(project);
        project.getGalleryFileKeys().add(uploadedResource.getKey());
        project.getResources().add(uploadedResource);
        project.setStorageSize(newStorageSize);

        projectRepository.save(project);

        return resourceMapper.toDto(resourceRepository.save(uploadedResource));
    }

    public List<ResourceReadDto> getAllProjectResources(long projectId) {
        Project project = getProject(projectId);
        if (project.getGalleryFileKeys().isEmpty()) {
            throw new DataValidationException("Галерея проекта пуста");
        }

        return project.getResources().stream().map(resourceMapper::toDto).toList();
    }

    @Transactional
    public void deleteResourceFromGallery(long projectId, long resourceId) {
        Project project = getProject(projectId);
        Resource findingResource = project.getResources().stream()
                .filter(resource -> resource.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Удаление невозможно: такого изображения нет в галерее проекта"));
        BigInteger storageSizeAfterDelete = project.getStorageSize().subtract(findingResource.getSize());
        project.setStorageSize(storageSizeAfterDelete);
        project.getGalleryFileKeys().remove(findingResource.getKey());

        amazonS3Client.deleteFile(findingResource.getKey());
        resourceRepository.deleteById(resourceId);
        projectRepository.save(project);
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
}
