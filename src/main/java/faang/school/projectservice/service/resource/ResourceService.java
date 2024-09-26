package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.StorageLimitException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.subproject.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@RequiredArgsConstructor
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ValidatorService validatorService;

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        validatorService.isProjectExists(projectId);
        Project project = projectRepository.findById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(newStorageSize, project.getMaxStorageSize());

        Resource resource = s3Service.uploadFile(file, project.getName());
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        return resourceMapper.mapToResourceDto(resource);
    }

    private void checkStorageSize(BigInteger storageSize, BigInteger maxStorageSize) {
        if (storageSize.compareTo(maxStorageSize) > 0) {
            System.out.println(storageSize);
            System.out.println(maxStorageSize);
            throw new StorageLimitException("Storage limit exceeded");
        }
    }
}
