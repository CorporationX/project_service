package faang.school.projectservice.validator;

import faang.school.projectservice.exception.FileDeleteException;
import faang.school.projectservice.exception.FileUploadException;
import faang.school.projectservice.exception.InvalidCurrentUserException;
import faang.school.projectservice.exception.StorageSpaceExceededException;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.resource.ResourceStatus;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.MessageFormat;

@Component
public class ProjectResourceValidator {

    public void validateResourceOnDelete(Resource resource) {
        if (resource.getStatus().equals(ResourceStatus.DELETED)) {
            throw new FileDeleteException(MessageFormat.format("File {0} already deleted", resource.getName()));
        }
    }

    public void validateFreeStorageCapacity(Project project, BigInteger fileSize) {
        if (fileSize.compareTo(project.getStorageSize()) > 0) {
            throw new StorageSpaceExceededException(
                    MessageFormat.format("Project {0} storage has not enough space", project.getId()));
        }
    }

    public void validateStorageCapacityOnUpdate(BigInteger storageCapacity, BigInteger fileSize, Resource resource) {
        if (fileSize.compareTo(storageCapacity) > 0) {
            String errorMessage = String.format(
                    "Impossible to update %s, project %d storage has not enough space",
                    resource.getName(), resource.getProject().getId());
            throw new StorageSpaceExceededException(errorMessage);
        }
    }

    public void validateFileOnUpdate(String resourceName, String fileOriginalName) {
        if (!resourceName.equals(fileOriginalName)) {
            throw new FileUploadException("File names don't match");
        }
    }

    public void validateIfUserCanChangeFile(Resource resource, long userId) {
        boolean notAProjectManager = !userIsProjectManager(resource.getProject(), userId);
        boolean notAFileCreator = !userIsFileCreator(resource, userId);

        if (notAProjectManager && notAFileCreator) {
            throw new InvalidCurrentUserException(
                    "You should be creator of a file or a project manager to change files");
        }
    }

    private boolean userIsFileCreator(Resource resource, long userId) {
        return resource.getCreatedBy().getUserId().equals(userId);
    }

    private boolean userIsProjectManager(Project project, long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .anyMatch(teamMember ->
                        teamMember.getRoles().contains(TeamRole.MANAGER) && teamMember.getUserId().equals(userId));
    }
}
