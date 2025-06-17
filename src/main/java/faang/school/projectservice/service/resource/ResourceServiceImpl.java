package faang.school.projectservice.service.resource;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final ResourceMapper resourceMapper;

    @Transactional
    @Override
    public ResourceDto uploadEntityFile(MultipartFile file, long id) {
        Project project = projectRepository.findById(id).orElseThrow();
        validateFreeSpace(project.getStorageSize(), project.getMaxStorageSize(), file.getSize());

        Optional<Resource> resource = resourceRepository.findByNameAndProjectId(file.getOriginalFilename(), id);
        if (resource.isEmpty()) {
            return createResource(file, project);
        }

        return updateResource(file, project, resource.get());
    }

    @Override
    public InputStream downloadFile(String fileKey) {
        resourceRepository.findByKey(fileKey).orElseThrow();
        return s3Service.downloadFile(fileKey);
    }

    @Transactional
    @Override
    public void deleteFile(String fileKey) {
        Resource resource = resourceRepository.findByKey(fileKey).orElseThrow();
        validateDeletionAccess(resource);

        s3Service.deleteFile(fileKey);

        deleteResource(resource);
        decreaseProjectSize(resource.getProject(), resource.getSize());
    }

    private ResourceDto createResource(MultipartFile file, Project project) {
        TeamMember teamMember = findTeamMember(project.getId());

        Resource uploadedResource = s3Service.uploadFile(file, project.getName());

        uploadedResource = createResourceByTeamMember(uploadedResource, teamMember, project);
        increaseProjectSize(project, uploadedResource.getSize());

        return resourceMapper.toDto(uploadedResource);
    }

    private ResourceDto updateResource(MultipartFile file, Project project, Resource existingResource) {
        TeamMember teamMember = findTeamMember(project.getId());

        if (existingResource.getKey() != null && !existingResource.getKey().isBlank()) {
            s3Service.deleteFile(existingResource.getKey());
        }
        Resource uploadedResource = s3Service.uploadFile(file, project.getName());
        decreaseProjectSize(project, existingResource.getSize());

        Resource updatedResource = updateResourceByTeamMember(existingResource, uploadedResource, teamMember);
        increaseProjectSize(project, updatedResource.getSize());

        return resourceMapper.toDto(updatedResource);
    }

    private void validateFreeSpace(BigInteger currentSize, BigInteger maxSize, long fileSize) {
        long freeSpace = maxSize.subtract(currentSize).abs().longValue();
        if (freeSpace > fileSize) {
            return;
        }

        throw new DataValidationException(String.format("Can't upload file. Storage size limit reached. File size: %d. Free space: %d",
                fileSize, freeSpace));
    }

    private void validateDeletionAccess(Resource resource) {
        if (
                resource.getProject().getOwnerId() == userContext.getUserId()
                        || resource.getCreatedBy().getUserId() == userContext.getUserId()
        ) {
            return;
        }

        throw new AccessDeniedException("Only owner and project manager can delete files.");
    }

    private TeamMember findTeamMember(long projectId) {
        if (userContext.getUserId() == 0) {
            throw new AccessDeniedException("User not authenticated");
        }

        return teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), projectId)
                .orElseThrow(() -> new AccessDeniedException("Only project members can upload files"));
    }

    private void increaseProjectSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().add(fileSize));
        projectRepository.save(project);
    }

    private void decreaseProjectSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().subtract(fileSize));
        projectRepository.save(project);
    }

    private void deleteResource(Resource resource) {
        resource.setKey(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setSize(BigInteger.ZERO);

        resourceRepository.save(resource);
    }

    private Resource createResourceByTeamMember(Resource resource, TeamMember teamMember, Project project) {
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setAllowedRoles(new ArrayList<>(teamMember.getRoles()));
        resource.setProject(project);

        return resourceRepository.save(resource);
    }

    private Resource updateResourceByTeamMember(Resource existingResource, Resource resource, TeamMember teamMember) {
        existingResource.setUpdatedBy(teamMember);
        existingResource.setSize(resource.getSize());
        existingResource.setKey(resource.getKey());
        existingResource.setStatus(ResourceStatus.ACTIVE);

        return resourceRepository.save(existingResource);
    }
}
