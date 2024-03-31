package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static faang.school.projectservice.service.s3.S3Service.bucketName;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ProjectMapper projectMapper;
    private final ResourceMapper resourceMapper;

    @SneakyThrows
    @Transactional
    public ResourceDto uploadCover(Long projectId, MultipartFile file) {
        final Long id = file.getSize() % 10; //
        ProjectDto projectDto = projectService.findProjectById(projectId);

        String folder = projectDto.getId() + projectDto.getName() + projectDto.getCoverImageId();
        Resource resource = S3Service.uploadFile(file, folder);
        resource.setProject(projectMapper.toEntity(projectDto));
        resource = resourceRepository.save(resource);

        projectDto.setCoverImageId(String.valueOf(id));
        projectRepository.save(projectMapper.toEntity(projectDto));

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", resourceId)));
        s3Service.deleteObject(bucketName, resource.getKey());
        log.info("File {} deleted from file storage", resource.getKey());
        resourceRepository.delete(resource);
        log.info("File {} deleted from data base", resource.getKey());
    }
}
