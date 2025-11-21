package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.s3.S3ServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3ServiceImpl s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional
    public ResourceDto addResource(long projectId, MultipartFile file) {
        ProjectDto projectDto = projectService.getProjectDtoById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        BigInteger newStorageSize = getNewStorageSize(projectId, file, projectDto);

        String folder = projectDto.getId() + projectDto.getName();

        ResourceDto resourceDto = s3Service.uploadFile(file, folder);
        resourceDto.setProject(projectDto);
        resourceRepository.save(resourceMapper.toEntity(resourceDto));

        projectDto.setStorageSize(newStorageSize);
        projectRepository.save(projectMapper.toEntity(projectDto));

        return resourceDto;
    }

    private static BigInteger getNewStorageSize(long projectId, MultipartFile file, ProjectDto projectDto) {
        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger currentStorageSize = projectDto.getStorageSize() == null ? BigInteger.ZERO : projectDto.getStorageSize();
        BigInteger maxStorageSize = projectDto.getMaxStorageSize();
        BigInteger newStorageSize = currentStorageSize.add(fileSize);
        if (maxStorageSize != null && newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalStateException("Storage limit exceeded for project " + projectId);
        }
        return newStorageSize;
    }

}
