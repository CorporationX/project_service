package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
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

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId);
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(newStorageSize, project.getStorageSize());
        s3Service.uploadFile(file, project.getName());
        return null;
    }

    private void checkStorageSize(BigInteger storageSize, BigInteger maxStorageSize) {
        if (storageSize.compareTo(maxStorageSize) > 0) {
            // TODO think about exception
//            throw new SizeLimitExceededException("Storage size low", storageSize.longValue(),maxStorageSize.l)

        }
    }
}
