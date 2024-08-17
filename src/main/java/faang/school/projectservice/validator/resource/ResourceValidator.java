package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.StorageSizeException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.*;
import faang.school.projectservice.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ResourceValidator {
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;

    public void validateStorageOverflow(Project project, MultipartFile file) {
        if (project.getMaxStorageSize() == null) {
            throw new RuntimeException("Project max storage size is null");
        }
        if (project.getStorageSize() == null) {
            throw new RuntimeException("Project storage size is null");
        }
        BigInteger resultSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        if (resultSize.compareTo(project.getMaxStorageSize()) > 0) {
            double neededSizeMb = resultSize.subtract(project.getMaxStorageSize()).doubleValue() / 1024 / 1024;
            DecimalFormat df = new DecimalFormat("#.##");
            throw new StorageSizeException("Storage is full! Needed " + df.format(neededSizeMb) + " MB more");
        }
    }

    public TeamMember getTeamMemberAndValidate(Project project, long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new AccessDeniedException("User " + userId + " are not a team member of project " + project.getId()));
    }

    public void validateCanUserDownload(Resource resource, TeamMember teamMember) {
        if (resource.getAllowedRoles().stream().noneMatch(teamRole -> teamMember.getRoles().contains(teamRole))) {
            throw new AccessDeniedException("User " + teamMember.getUserId() + " has not access to resource " + resource.getId());
        }
    }

    public void validateIsUpdateRequired(MultipartFile file, List<TeamRole> allowedTeamRoles) {
        if (file == null && allowedTeamRoles == null) {
            throw new IllegalArgumentException("No update required");
        }
    }


    public void getAllowedRolesAndValidate(List<TeamRole> allowedTeamRoles, TeamMember teamMember) {
        if (allowedTeamRoles == null || allowedTeamRoles.isEmpty()) {
            allowedTeamRoles = teamMember.getRoles();
        }
    }

    public Resource getResourceAndValidate(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource with id " + resourceId + " not found"));
        if (resource.getStatus() == ResourceStatus.DELETED) {
            throw new EntityNotFoundException("Resource with id " + resourceId + " was deleted");
        }
        return resource;
    }

    public void validateCanUserRemoveOrUpdate(TeamMember teamMember, Resource resource) {
        if (!teamMember.getRoles().contains(TeamRole.MANAGER)
                && !teamMember.getRoles().contains(TeamRole.OWNER)
                && !teamMember.getId().equals(resource.getCreatedBy().getId())) {
            throw new AccessDeniedException("A file can only be deleted by its creator or project manager");
        }
    }

    public Project getProjectAndValidate(Long projectId, Resource resource) {
        Project project = resource.getProject();
        if (project == null) {
            throw new EntityNotFoundException("Project " + projectId + " is null");
        }
        if (!project.getId().equals(projectId)) {
            throw new IllegalArgumentException("Resource " + resource.getId() + " is owned by another project");
        }
        return project;
    }

    public void validateIsFileExist(String bucketName, String path) {
        if (!s3Service.doesObjectExist(bucketName, path)) {
            throw new RuntimeException("File" + path + "not exist");
        }
    }

    public void validateResourceStatus(ResourceStatus resourceStatus) {
        if (resourceStatus == ResourceStatus.DELETED) {
            throw new IllegalArgumentException("Status cannot be updated to DELETED");
        }
    }
}
