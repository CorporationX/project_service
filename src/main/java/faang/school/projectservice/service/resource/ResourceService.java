package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.AmazonS3Service;
import faang.school.projectservice.validator.service.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final AmazonS3Service amazonService;
    private final ResourceRepository resourceRepository;
    private final ResourceValidator resourceValidator;

    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        resourceValidator.checkStorageSize(project);
        Resource resource = amazonService.uploadFile(file, getFolderName(project.getId(), file.getContentType()));
        project.getResources().add(resource);
        resourceRepository.save(resource);
        projectRepository.save(project);
        return resourceMapper.toDto(resource);
    }

    public ResourceDto deleteResource(Long projectId, Long resourceId) {
        Project project = projectRepository.getProjectById(projectId);
        List<Resource> resources = project.getResources();

        return null;
    }

    public ResourceDto getResource(Long projectId, Long resourceId) {
        Project project = projectRepository.getProjectById(projectId);
        List<Resource> resources = project.getResources();
        Resource resource = resources.stream()
                .filter(r -> r.getId().equals(resourceId))
                .findFirst()
                .orElse(null);
        return null;
    }

    private String getFolderName(long postId, String contentType) {
        return String.format("%s-%s", postId, contentType);
    }
}
