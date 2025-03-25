package faang.school.projectservice.service.resource;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.minio.MinioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final MinioService s3Service;

    @Override
    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new NotFoundException("There is no project with ID " + projectId)
        );

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = 1 + "_" + "My project"; //project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.setCoverImageId(resource.getId() + resource.getName());
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    @Override
    public InputStream downloadResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(
                () -> new NotFoundException("There is no resource with ID " + resourceId)
        );
        return s3Service.downloadFile(resource.getKey());
    }

    @Override
    public ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file) {

        return null;
    }

    @Override
    public void deleteResource(Long resourceId, Long userId) {

    }

    private void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSize) {

    }
}
