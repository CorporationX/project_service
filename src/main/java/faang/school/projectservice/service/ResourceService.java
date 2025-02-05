package faang.school.projectservice.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private static final long FILE_MAX_COUNT_IN_PROJECT_GALLERY = 50;

    private final S3Service amazonS3Client;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceReadDto uploadResource(long projectId, MultipartFile file) {
        validateResource(file);

        Project project = projectService.getProject(projectId);
        String folder = projectId + project.getName();
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(fileSize);

        validateProjectStorageSize(newStorageSize, project, fileSize);
        validateStorageSpace(folder);

        Resource uploadedResource = amazonS3Client.uploadFile(file, folder);
        uploadedResource.setProject(project);
        project.getGalleryFileKeys().add(uploadedResource.getKey());
        project.getResources().add(uploadedResource);
        project.setStorageSize(newStorageSize);

        projectRepository.save(project);

        return resourceMapper.toDto(resourceRepository.save(uploadedResource));
    }

    public List<ResourceReadDto> getAllProjectResources(long projectId) {
        Project project = projectService.getProject(projectId);
        if (project.getGalleryFileKeys().isEmpty()) {
            throw new DataValidationException("Галерея проекта пуста");
        }

        return project.getResources().stream().map(resourceMapper::toDto).toList();
    }

    @Transactional
    public void deleteResource(long projectId, long resourceId) {
        Project project = projectService.getProject(projectId);
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

    private static void validateProjectStorageSize(BigInteger newStorageSize, Project project, BigInteger fileSize) {
        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new DataValidationException(String.format(
                    "Загрузка невозможна: максимальный размер хранилища %d, размер файла %d",
                    project.getMaxStorageSize(), fileSize));
        }
    }

    private static void validateResource(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DataValidationException("Загрузка невозможна: файл пустой");
        }
    }

    private void validateStorageSpace(String folder) {
        List<S3ObjectSummary> summary = amazonS3Client.getAllObject(folder);
        if (summary.size() >= FILE_MAX_COUNT_IN_PROJECT_GALLERY) {
            throw new DataValidationException(
                    String.format("Файл не может быть добавлен в хранилище, " +
                            "так как превышен максимальный лимит в %d файлов", FILE_MAX_COUNT_IN_PROJECT_GALLERY));
        }
    }
}
