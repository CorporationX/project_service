package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.minio.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final MinioService minioService;
    private final ResourceRepository resourceRepository;

    @Transactional
    public Project addCoverImage(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        Resource resource = minioService.uploadFile(file, "project-covers");
        resource.setProject(project);
        Resource savedResource = resourceRepository.save(resource);
        project.setCoverImageId(savedResource.getKey());
        return projectRepository.save(project);
    }
}