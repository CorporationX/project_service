package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.NoAccessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ResourceValidator {
    @Value("${services.s3.maxFreeStorageSize}")
    private BigInteger maxFreeStorageSize;

    public void validateMaxFreeStorageSize(Project project, Long fileLength) {
        BigInteger newLength = project.getStorageSize().add(BigInteger.valueOf(fileLength));
        if (newLength.compareTo(maxFreeStorageSize) > 0) {
            throw new DataValidationException("Project storage is full");
        }
    }

    public void validateDownloadFilePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getProject().equals(teamMember.getTeam().getProject())
                && !teamMember.getRoles().contains(TeamRole.MANAGER)
                && !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new NoAccessException("Only project members, managers and owners can download files from storage.");
        }
    }

    public void validateDeleteFilePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getCreatedBy().equals(teamMember)
                && !teamMember.getRoles().contains(TeamRole.MANAGER)
                && !teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new NoAccessException("TeamMember with id: " + teamMember.getId() +
                    " has no permissions to delete resource with id: " + resource.getId());
        }
    }
}
