package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
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
        Resource resource = minioService.uploadFile(file);
        resource.setProject(project);
        Resource savedResource = resourceRepository.save(resource);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        //checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());
        project.setStorageSize(newStorageSize);
        project.setCoverImageId(savedResource.getKey());
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Transactional
    public ProjectDto updateCoverImage(Long projectId, MultipartFile file) {



        return null;
    }
}