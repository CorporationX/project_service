package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.exception.NoAccessExceptionMessage;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class TeamMemberResourceValidator {
    public void validateDownloadFilePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getProject().equals(teamMember.getTeam().getProject())) {
            throw new NoAccessException(NoAccessExceptionMessage.DOWNLOAD_PERMISSION_ERROR.getMessage());
        }
    }

    public void validateDeleteFilePermission(TeamMember teamMember, Resource resource) {
        if (notUploader(teamMember, resource) && notManagerFromUploaderProject(teamMember, resource)
                && notOwnerFromUploaderProject(teamMember, resource)) {
            throw new NoAccessException(NoAccessExceptionMessage.DELETE_PERMISSION_ERROR.getMessage());
        }
    }

    private static boolean notUploader(TeamMember teamMember, Resource resource) {
        return !resource.getCreatedBy().equals(teamMember);
    }

    private static boolean notManagerFromUploaderProject(TeamMember teamMember, Resource resource) {
        return !teamMember.getRoles().contains(TeamRole.MANAGER) || !teamMember.getTeam().getProject().equals(resource.getProject());
    }

    private static boolean notOwnerFromUploaderProject(TeamMember teamMember, Resource resource) {
        return !teamMember.getRoles().contains(TeamRole.OWNER) || !teamMember.getTeam().getProject().equals(resource.getProject());
    }
}
