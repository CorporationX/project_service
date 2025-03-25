package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.minio.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MinioService minioService;
    private final ResourceRepository resourceRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectDto addCoverImage(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        if (project.getCoverImageId() != null) {
            throw new IllegalStateException("Project already has a cover image. Use update instead.");
        }
        return setCoverImage(project, file);
    }

    @Transactional
    public ProjectDto updateCoverImage(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        if (project.getCoverImageId() == null) {
            throw new IllegalStateException("Project has no cover image to update. Use add instead.");
        }

        resourceRepository.findByKey(project.getCoverImageId())
                .ifPresent(resource -> {
                    resource.setStatus(ResourceStatus.INACTIVE);
                    resourceRepository.save(resource);
                });

        return setCoverImage(project, file);
    }

    @Transactional
    public ProjectDto softDeleteCoverImage(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        if (project.getCoverImageId() != null) {
            resourceRepository.findByKey(project.getCoverImageId())
                    .ifPresent(resource -> {
                        resource.setStatus(ResourceStatus.INACTIVE);
                        resourceRepository.save(resource);
                    });
            project.setCoverImageId(null);
            return projectMapper.toDto(projectRepository.save(project));
        }

        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto hardDeleteCoverImage(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        if (project.getCoverImageId() != null) {
            resourceRepository.findByKey(project.getCoverImageId())
                    .ifPresent(resource -> {
                        BigInteger fileSize = resource.getSize();
                        minioService.deleteFile(project.getCoverImageId());
                        resourceRepository.delete(resource);

                        BigInteger currentStorageSize = project.getStorageSize() != null
                                ? project.getStorageSize() : BigInteger.ZERO;
                        project.setStorageSize(currentStorageSize.subtract(fileSize));
                    });
            project.setCoverImageId(null);
            return projectMapper.toDto(projectRepository.save(project));
        }
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto setCoverImage(Project project, MultipartFile file) {
        MinioService.CompressResult compressResult = minioService.compressImageIfNeeded(file);

        BigInteger newFileSize = BigInteger.valueOf(compressResult.getSize());
        BigInteger currentStorageSize = project.getStorageSize() != null ? project.getStorageSize() : BigInteger.ZERO;
        BigInteger maxStorageSize = project.getMaxStorageSize();

        BigInteger updatedStorageSize = currentStorageSize.add(newFileSize);
        if (maxStorageSize != null && updatedStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalStateException("Compressed file size exceeds project storage limit: " +
                    updatedStorageSize + " > " + maxStorageSize);
        }

        String key = minioService.uploadFile(
                compressResult.getFile(), compressResult.getContentType());

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(newFileSize);
        resource.setProject(project);
        resource.setName(file.getOriginalFilename());
        resource.setType(ResourceType.getResourceType(compressResult.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resourceRepository.save(resource);

        project.setCoverImageId(key);
        project.setStorageSize(updatedStorageSize);
        return projectMapper.toDto(projectRepository.save(project));
    }
}