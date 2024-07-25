package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final TeamMemberService teamMemberService;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final ResourceRepository repository;
    private final ResourceMapper mapper;
    private final S3Service s3Service;

    public String getResource(long id) {
        Resource resource = this.getOneOrThrow(id);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new IllegalArgumentException("Resource is deleted");
        }

        if (!s3Service.isObjectExist(resource.getKey())) {
            throw new IllegalArgumentException("Resource does not exist");
        }

        return s3Service.getFileUrl(resource.getKey());
    }

    @Transactional
    public ResourceDto createResource(long userId, long projectId, MultipartFile file) {
        Project project = projectService.getOneOrThrow(projectId);
        TeamMember teamMember = teamMemberService.getOneByUserIdAndProjectIdOrThrow(userId, project.getId());

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(fileSize);

        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new IllegalArgumentException("The size of the new file exceeds the total size of the storage");
        }

        String key = s3Service.uploadFile(file);

        LocalDateTime now = LocalDateTime.now();
        Resource resource = Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(fileSize)
                .allowedRoles(new ArrayList<>(teamMember.getRoles()))
                .type(ResourceType.getResourceType(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(now)
                .updatedAt(now)
                .project(project)
                .build();

        Resource newResource = repository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return mapper.toDto(newResource);
    }

    @Transactional
    public ResourceDto updateResource(long userId, long resourceId, MultipartFile file) {
        Resource resource = this.getOneOrThrow(resourceId);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new IllegalArgumentException("Resource is deleted");
        }

        Project project = projectService.getOneOrThrow(resource.getProject().getId());
        TeamMember teamMember = teamMemberService.getOneByUserIdAndProjectIdOrThrow(userId, project.getId());

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize()).add(fileSize);

        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new IllegalArgumentException("The size of the new file exceeds the total size of the storage");
        }

        s3Service.deleteFile(resource.getKey());
        String key = s3Service.uploadFile(file);

        LocalDateTime now = LocalDateTime.now();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(fileSize);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(now);

        Resource newResource = repository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return mapper.toDto(newResource);
    }

    @Transactional
    public void deleteResource(long userId, long resourceId) {
        Resource resource = this.getOneOrThrow(resourceId);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            return;
        }

        Project project = projectService.getOneOrThrow(resource.getProject().getId());
        TeamMember teamMember = teamMemberService.getOneByUserIdAndProjectIdOrThrow(userId, project.getId());

        // The owner of the resource or the project manager can delete the resource
        if (!resource.getCreatedBy().getId().equals(teamMember.getId()) && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException("You do not have permission to delete this resource");
        }

        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize());

        s3Service.deleteFile(resource.getKey());

        LocalDateTime now = LocalDateTime.now();
        resource.setKey(null);
        resource.setSize(null);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(now);
        resource.setStatus(ResourceStatus.DELETED);

        repository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
    }

    public Resource getOneOrThrow(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Resource with id %s not found", id)));
    }
}
