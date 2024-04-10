package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.MessageError;
import faang.school.projectservice.exception.SizeExceeded;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.S3.S3ServiceImpl;
import faang.school.projectservice.validator.ResourceValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final S3ServiceImpl s3Service;
    private final ResourceRepository resourceRepository;
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;
    private final UserServiceClient userServiceClient;
    private final ResourceValidator resourceValidator;

    @Transactional
    public ResourceDto uploadResource(Long projectId, MultipartFile file, long userId) {
        Project project = projectService.getProjectById(projectId);
        TeamMember author = resourceValidator.validateForTeamMemberExistence(userId, projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkIfStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);

        resource.setProject(project);
        resource.setCreatedBy(author);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectService.save(project);//should I add this method to synchronize the updated info with database or @Transactional is enough

        return resourceMapper.toDto(resource);

    }

    @Transactional
    public InputStream downloadResource(long sourceId) {
        Resource resource = resourceRepository.findById(sourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with " + sourceId + " id not found"));
        return s3Service.downloadFile(resource.getKey());
    }


    @Transactional
    public void deleteResource(long resourceId, long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with " + userId + " id not found");
        }

        Resource resource = resourceRepository.getReferenceById(resourceId);
        resourceValidator.validateForOwner(userId, resource);
        String key = resource.getKey();

        s3Service.deleteFile(key);
        resourceRepository.delete(resource);

        Project project = resource.getProject();
        BigInteger newSize = project.getStorageSize().subtract(resource.getSize());
        project.setStorageSize(newSize);
    }


    private void checkIfStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (0 > maxStorageSize.compareTo(newStorageSize)) {
            throw new SizeExceeded(MessageError.FILE_STORAGE_CAPACITY_EXCEEDED);
        }
    }

}
