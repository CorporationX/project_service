package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.StorageLimitException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ResourceService resourceService;

    public Optional<ProjectDto> getProjectById(long projectId) {
        return projectRepository.findById(projectId).map(projectMapper::toDto);
    }

    @Retryable(retryFor = ObjectOptimisticLockingFailureException.class)
    public ResourceDto addCover(long projectId, MultipartFile file) {
        ProjectDto projectDto = getProjectById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
        ResourceDto resourceDto = resourceService.addCover(projectDto, file);
        try {
            BigInteger newStorageSize = getNewStorageSize(resourceDto.getSize().longValue(), projectDto);
            projectDto.setStorageSize(newStorageSize);
            projectDto.setCoverImageId(resourceDto.getKey());
            projectRepository.save(projectMapper.toEntity(projectDto));
        } catch (ObjectOptimisticLockingFailureException e) {
            log.info("Optimistic lock by database {}", e.getMessage());
            throw e;
        }
        return resourceDto;
    }

    private BigInteger getNewStorageSize(long delta, ProjectDto projectDto) {
        BigInteger fileSize = BigInteger.valueOf(delta);
        BigInteger currentStorageSize =
                projectDto.getStorageSize() == null ? BigInteger.ZERO : projectDto.getStorageSize();
        BigInteger maxStorageSize = projectDto.getMaxStorageSize();
        BigInteger newStorageSize = currentStorageSize.add(fileSize);
        if (maxStorageSize != null && newStorageSize.compareTo(maxStorageSize) > 0) {
            log.info("Storage limit exceeded for project. projectId={}, maxStorageSize={}, " +
                            "currentStorageSize={}, fileSize={}, requestedStorageSize={}",
                    projectDto.getId(),
                    maxStorageSize,
                    currentStorageSize,
                    fileSize,
                    newStorageSize);
            throw new StorageLimitException(String.format(
                    "Storage limit exceeded for project %d. " +
                            "Max storage size is %d, but you want got %d",
                    projectDto.getId(),
                    maxStorageSize.intValue(),
                    newStorageSize.intValue()));
        }
        return newStorageSize;
    }
}
