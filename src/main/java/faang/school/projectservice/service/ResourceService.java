package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.CreateResourceDto;
import faang.school.projectservice.dto.resource.ResourceResultDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.mapper.ResourceResultMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MinioService minioService;
    private final ResourceMapper resourceMapper;
    private final ResourceResultMapper resourceResultMapper;

    @Value("${app.max-project-storage-size}")
    private long defaultMaxProjectStorageSize;

    public Resource getResourceRefById(long id) {
        return resourceRepository.getReferenceById(id);
    }

    public ResourceResultDto uploadResource(MultipartFile file, Long projectId, Long teamMemberId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("The file is empty");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The project was not found"));

        BigInteger currentSize = project.getStorageSize() == null ? BigInteger.ZERO : project.getStorageSize();
        BigInteger fileSize = BigInteger.valueOf(file.getSize());

        BigInteger maxSize = project.getMaxStorageSize() == null
                ? BigInteger.valueOf(defaultMaxProjectStorageSize)
                : project.getMaxStorageSize();
        if (currentSize.add(fileSize).compareTo(maxSize) > 0) {
            throw new IllegalArgumentException("The project storage limit has been exceeded");
        }

        String key = String.format("project-%d/%s_%s", projectId, UUID.randomUUID(), file.getOriginalFilename());

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes())) {
            minioService.uploadFile(byteArrayInputStream, key, file.getContentType(), file.getSize());
        } catch (IOException e) {
            log.error("Error receiving the file stream", e);
            throw new RuntimeException("Error receiving the file stream", e);
        } catch (Exception e) {
            log.error("Error uploading a file to MinIO", e);
            throw new RuntimeException("Error uploading a file to MinIO", e);
        }

        Resource resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(TeamRole.getAll())
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(TeamMember.builder().id(teamMemberId).build())
                .updatedBy(TeamMember.builder().id(teamMemberId).build())
                .project(Project.builder().id(projectId).build())
                .build();
        Resource savedResource = resourceRepository.save(resource);

        project.setStorageSize(currentSize.add(fileSize));
        projectRepository.save(project);

        log.info("The file '{}' has been successfully uploaded to the project '{}'",
                file.getOriginalFilename(), project.getName());
        return resourceResultMapper.toResultDto(savedResource);
    }

    public void deleteResource(Long resourceId, Long teamMemberId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The resource was not found"));

        TeamMember currentUser = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The user was not found"));

        if (!resource.getCreatedBy().getId().equals(teamMemberId)
                && !currentUser.getRoles().contains(TeamRole.MANAGER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The user does not have the rights to delete this file.");
        }

        if (resource.getKey() != null) {
            minioService.removeFile(resource.getKey());
        }

        Project project = resource.getProject();
        BigInteger currentSize = Objects.requireNonNullElse(project.getStorageSize(), BigInteger.ZERO);
        BigInteger fileSize = Objects.requireNonNullElse(resource.getSize(), BigInteger.ZERO);
        if (currentSize.compareTo(fileSize) >= 0) {
            project.setStorageSize(currentSize.subtract(fileSize));
        } else {
            project.setStorageSize(BigInteger.ZERO);
        }
        projectRepository.save(project);

        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(currentUser);

        resourceRepository.save(resource);

        log.info("The resource with the ID {} was successfully deleted by the user with the ID {}",
                resourceId, teamMemberId);
    }
}
