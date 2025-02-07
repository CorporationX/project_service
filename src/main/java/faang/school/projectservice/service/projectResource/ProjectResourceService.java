package faang.school.projectservice.service.projectResource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.service.amazonS3Service.AmazonS3Service;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectResourceService {
    private final AmazonS3Service amazonS3Service;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final ResourceRepository resourceRepository;

    @Transactional
    public Resource addFile(long projectResourceId, long userId, MultipartFile file) {
        Project project = projectService.getProject(projectResourceId);

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger storageSize = project.getStorageSize().add(fileSize);

        checkStorageForEnoughMemory(storageSize, project.getMaxStorageSize());
        project.setStorageSize(storageSize);

        String key = String.format("%s/%d %s", projectResourceId + project.getName(),
            System.currentTimeMillis(),
            file.getOriginalFilename());

        TeamMember author = getTeamMember(userId, projectResourceId);

        Resource resource = amazonS3Service.addResource(file, key);
        resource.setProject(project);
        resource.setCreatedBy(author);
        resource.setUpdatedBy(author);

        resourceRepository.save(resource);
        projectService.saveProject(project);

        log.info("The file has been added " + resource.getName());
        return resource;
    }

    public Resource updateFile(long projectResourceId, Long userId, MultipartFile file) {
        Resource resource = getResource(projectResourceId);
        Project project = resource.getProject();

        BigInteger fileSize = BigInteger.valueOf(file.getSize());
        BigInteger storageSize = project.getStorageSize().add(fileSize);
        storageSize = storageSize.subtract(resource.getSize());
        checkStorageForEnoughMemory(storageSize, project.getMaxStorageSize());

        project.setStorageSize(storageSize);

        TeamMember updatedBy = getTeamMember(userId, project.getId());

        resource.setUpdatedBy(updatedBy);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setSize(BigInteger.valueOf(file.getSize()));

        resourceRepository.save(resource);
        projectService.saveProject(project);

        amazonS3Service.updateResource(file, resource.getKey());

        log.info("The file has been changed " + resource.getName());
        return resource;
    }

    public Resource removeFile(long projectResourceId, long userId) {
        Resource resource = getResource(projectResourceId);
        Project project = resource.getProject();

        TeamMember teamMember = getTeamMember(userId, project.getId());
        checkAccessForRemoval(userId, resource, teamMember.getRoles());

        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUpdatedBy(teamMember);

        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        resource.setSize(BigInteger.valueOf(0));

        amazonS3Service.completeRemoval(resource.getKey());
        resource.setKey(null);

        resourceRepository.save(resource);
        projectService.saveProject(project);

        log.info("The file has been deleted " + resource.getName());
        return resource;
    }

    private void checkStorageForEnoughMemory(@NotNull BigInteger currentStorageSize,
                                             @NotNull BigInteger maxStorageSize) {
        if (currentStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Exceeded the 2GB volume");
        }
    }

    private void checkAccessForRemoval(long userId, @NotNull Resource resource,
                                       @NotNull List<TeamRole> teamRoles) {
        if (userId != resource.getCreatedBy().getUserId() &&
            !teamRoles.contains(TeamRole.MANAGER)) {
            throw new IllegalArgumentException(
                "Access error, only the author or manager can delete the file");
        }
    }

    private TeamMember getTeamMember(long userId, long projectId) {
        return teamMemberService.getTeamMemberByIdAndProjectId(userId, projectId);
    }

    private Resource getResource(long resourceId) {
        return resourceRepository.findById(resourceId)
            .orElseThrow(
                () -> new EntityNotFoundException("Resource not found by id " + resourceId));
    }

}
