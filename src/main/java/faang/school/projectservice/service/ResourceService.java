package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ProjectMapper projectMapper;
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;

    public ProjectDto addCoverToProject(MultipartFile file, Long projectId) {
        ProjectDto project = projectService.findProjectById(projectId);
        Project projectEntity = projectMapper.toEntity(project);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(projectEntity);
        resource = resourceRepository.save(resource);

        projectEntity.setCoverImageId(resource.getId());
        projectRepository.save(projectEntity);

        return projectMapper.toDto(projectEntity);
    }
}
