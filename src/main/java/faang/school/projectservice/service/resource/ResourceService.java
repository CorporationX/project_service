package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.dto.resource.ResourceDownloadDto;
import faang.school.projectservice.exception.resource.ResourceDeleteNotAllowedException;
import faang.school.projectservice.exception.resource.ResourceDeletedException;
import faang.school.projectservice.exception.resource.ResourceDownloadException;
import faang.school.projectservice.exception.resource.ResourceUploadException;
import faang.school.projectservice.exception.resource.StorageLimitExceededException;
import faang.school.projectservice.exception.resource.UnauthorizedFileUploadException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional(readOnly = true)
    public ResourceDownloadDto downloadResource(Long id) {
        Resource resource = findResource(id);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new ResourceDeletedException();
        }

        try {
            S3Object fileObject = s3Service.download(resource.getKey());

            return new ResourceDownloadDto(
                    fileObject.getObjectContent().readAllBytes(),
                    MediaType.parseMediaType(fileObject.getObjectMetadata().getContentType()),
                    resource.getName()
            );
        } catch (Exception exception) {
            log.error("Error download resource {} message {}", resource.getId(), exception.getMessage());
            throw new ResourceDownloadException();
        }
    }

    @Transactional
    public void deleteResource(Long id, Long memberId) {
        Resource resource = findResource(id);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new ResourceDeletedException();
        }

        TeamMember teamMember = teamMemberRepository.findById(memberId);
        if (!resource.getCreatedBy().getId().equals(teamMember.getId())
                && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new ResourceDeleteNotAllowedException(resource.getProject().getId());
        }

        Project project = projectRepository.getProjectById(resource.getProject().getId());

        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize());

        s3Service.delete(resource.getKey());

        LocalDateTime now = LocalDateTime.now();
        resource.setKey(null);
        resource.setSize(null);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(now);
        resource.setStatus(ResourceStatus.DELETED);

        resourceRepository.save(resource);

        projectService.updateStorageSize(project.getId(), newStorageSize);
    }

    @Transactional
    public Resource updateResource(MultipartFile file, Long id, Long memberId) {
        Resource resource = findResource(id);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new ResourceDeletedException();
        }

        Project project = projectRepository.getProjectById(resource.getProject().getId());

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger newStorageSize = project.getStorageSize()
                .subtract(resource.getSize())
                .add(fileSize);

        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new StorageLimitExceededException();
        }

        String key = makeResourceKey(project, file);
        TeamMember teamMember = teamMemberRepository.findById(memberId);

        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(fileSize);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(LocalDateTime.now());
        resourceRepository.save(resource);
        projectService.updateStorageSize(project.getId(), newStorageSize);

        s3Service.delete(resource.getKey());

        try {
            s3Service.upload(file, key);
        } catch (RuntimeException exception) {
            throw new ResourceUploadException();
        }

        return resource;
    }

    @Transactional
    public Resource createResource(MultipartFile file, Long projectId, Long memberId) {
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = BigInteger.valueOf(file.getSize()).add(project.getStorageSize());

        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new StorageLimitExceededException();
        }

        TeamMember teamMember = teamMemberRepository.findById(memberId);

        if (!teamMember.getTeam().getProject().equals(project)) {
            throw new UnauthorizedFileUploadException();
        }

        String key = makeResourceKey(project, file);

        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setAllowedRoles(teamMember.getRoles().isEmpty() ? new ArrayList<>() : teamMember.getRoles());
        resource.setCreatedBy(teamMember);
        resource.setProject(project);
        resource.setCreatedAt(LocalDateTime.now());
        resourceRepository.save(resource);
        projectService.updateStorageSize(projectId, newStorageSize);

        try {
            s3Service.upload(file, key);
        } catch (RuntimeException exception) {
            throw new ResourceUploadException();
        }

        return resource;
    }

    public Resource findResource(Long id) {
        return resourceRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Resource not found by id: %s", id))
        );
    }

    private String makeResourceKey(Project project, MultipartFile file) {
        String folderName = String.format("%s_%s", project.getId(), project.getName());
        return String.format("%s/%d_%s", folderName, System.nanoTime(), file.getOriginalFilename());
    }
}
