package faang.school.projectservice.service.resource;

import faang.school.projectservice.exception.StorageSizeExceedException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.CloudService;
import faang.school.projectservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final CloudService cloudService;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;

    @Override
    public InputStream downloadResource(long resourceId) {
        Resource resource = findResourceById(resourceId);
        return cloudService.downloadFile(resource.getKey());
    }

    @Override
    public Resource uploadResource(MultipartFile file, long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project with id %d was not found".formatted(projectId)));
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceed(project, newStorageSize);
        String folder = project.getName();

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        Resource resource = cloudService.uploadFile(file, folder);
        resource.setProject(project);
        resourceRepository.save(resource);
        return resource;
    }

    @Override
    public Resource deleteResource(long resourceId) {
        Resource resource = findResourceById(resourceId);
        cloudService.deleteFile(resource.getKey());
        return resource;
    }

    private Resource findResourceById(long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NoSuchElementException("Resource with id %d was not found".formatted(resourceId)));
    }

    private void checkStorageSizeExceed(Project project, BigInteger newStorageSize) {
        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new StorageSizeExceedException("New storage size %d for project %s exceed".formatted(newStorageSize, project));
        }
    }
}