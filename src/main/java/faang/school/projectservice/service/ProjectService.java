package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.ResourceReadDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.amazonS3Service.AmazonS3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@Data
public class ProjectService {

    private final ResourceValidator resourceValidator;
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final ResourceRepository resourceRepository;
    private final AmazonS3Service amazonS3Client;


    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Project with id {} not found", id);
                    return new NoSuchElementException("Project with id " + id + " not found");
                });
        }

    @Transactional
    public ResourceReadDto uploadResourceToGallery(long projectId, MultipartFile file) {
        resourceValidator.validateResource(file);


        Project project = getProject(projectId);

        //Путь с форматом "projects/123/my-cool-project"
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
        return resourceMapper.todo(resourceRepository.save(uploadedResource));
    }

    private Project getProject(Long id) {
        return projectRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with  id = " + id + " not found"));
    }

    private Resource uploadResourceToStorage(MultipartFile file, String folder) {
        String uploadedResourceKey = amazonS3Client.uploadFile(file, folder);

        return Resource.builder()
                .key(uploadedResourceKey)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .status(ResourceStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
