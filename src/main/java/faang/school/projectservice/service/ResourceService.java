package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import faang.school.projectservice.validation.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final S3ServiceImpl s3Service;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;

    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectService.getProject(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        resourceValidator.checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectService.save(project);

        return resourceMapper.resourceToDto(resource);
    }

    public ResourceDto updateResource(Long userId, Long resourceId, MultipartFile file) {
        Resource resourceFromDB = resourceRepository.getOne(resourceId);
        Project project = resourceFromDB.getProject();

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()))
                .subtract(resourceFromDB.getSize());
    }

    public InputStream downloadResource(Long resourceId) {
        Resource resourc = getRosoursById(resourceId);
        return s3Service.downloadFile(resourc.getKey());
    }
}
