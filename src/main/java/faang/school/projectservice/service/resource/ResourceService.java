package faang.school.projectservice.service.resource;

import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.service.resource.ResourceErrorMessage.FILE_SIZE_EXCEED_STORAGE_VALUE;
import static faang.school.projectservice.service.resource.ResourceErrorMessage.NOT_FOUND_PROJECT;
import static faang.school.projectservice.service.resource.ResourceErrorMessage.NOT_FOUND_RESOURCE;
import static faang.school.projectservice.service.resource.ResourceErrorMessage.NOT_FOUND_TEAM_MEMBER;


@RequiredArgsConstructor
@Service
public class ResourceService {
    private final S3Service s3Service;

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    private final String folderPattern = "%s-%s";

    @Transactional
    public Resource addResource(Long projectId, Long userId, MultipartFile file) {
        Project project = getProjectById(projectId);
        TeamMember member = getTeamMember(userId, projectId);

        String folder = String.format(folderPattern, projectId, project.getName());
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setName(file.getOriginalFilename());
        resource.setProject(project);
        resource.setCreatedBy(member);
        resource.setUpdatedBy(member);
        resource.setAllowedRoles(List.copyOf(member.getRoles()));
        resource = resourceRepository.save(resource);
        setNewStorageSize(project, file.getSize());

        return resource;
    }

    @Transactional
    public Resource updateResource(Long resourceId, Long projectId, Long userId, MultipartFile file) {
        Project project = getProjectById(projectId);
        TeamMember member = getTeamMember(userId, projectId);
        Resource oldResource = getResourceById(resourceId);

        long sizeDifference = file.getSize() - oldResource.getSize().longValue();
        String folder = String.format(folderPattern, projectId, project.getName());

        s3Service.deleteFile(oldResource.getKey());
        Resource newResource = s3Service.uploadFile(file, folder);

        oldResource.setKey(newResource.getKey());
        oldResource.setUpdatedAt(newResource.getUpdatedAt());
        oldResource.setUpdatedBy(member);
        oldResource = resourceRepository.save(oldResource);

        setNewStorageSize(project, sizeDifference);

        return oldResource;
    }

    @Transactional
    public void deleteResource(Long resourceId, Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        TeamMember member = getTeamMember(userId, projectId);
        Resource resource = getResourceById(resourceId);

        s3Service.deleteFile(resource.getKey());

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(member);
        resourceRepository.save(resource);

        setNewStorageSize(project, resource.getSize().negate().longValue());
    }

    private void setNewStorageSize(Project project, Long fileSize) {
        BigInteger newStorageSize = project.getStorageSize()
                .add(BigInteger.valueOf(fileSize));

        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new FileException(String.format(FILE_SIZE_EXCEED_STORAGE_VALUE,
                    newStorageSize, project.getMaxStorageSize()));
        }

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_PROJECT, projectId)));
    }

    private TeamMember getTeamMember(Long userId, Long projectId) {
        return Optional.ofNullable(teamMemberRepository.findByUserIdAndProjectId(userId, projectId))
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_TEAM_MEMBER, userId)));
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(NOT_FOUND_RESOURCE, resourceId)));
    }
}
