package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.image.ImageResizer;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ProjectMapper projectMapper;
    private final ImageResizer imageResizer;


    @SneakyThrows
    @Transactional
    public Resource addACoverToTheProject(Long projectId, MultipartFile file) {
        ProjectDto projectDto = projectService.findProjectById(projectId);
        imageResizer.resizeAndCompressImage(file);

        String folder = projectDto.getId() + projectDto.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(projectMapper.toEntity(projectDto));
        resource = resourceRepository.save(resource);

        projectDto.setCoverImageId(file.getName());
        projectRepository.save(projectMapper.toEntity(projectDto));

        return resource;
    }

    public void deleteResource(String key) {
        s3Service.deleteFile(key);
    }
}
