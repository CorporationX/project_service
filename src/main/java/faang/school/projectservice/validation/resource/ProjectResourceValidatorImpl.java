package faang.school.projectservice.validation.resource;

import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.NoAccessException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.property.AmazonS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
@RequiredArgsConstructor
public class ProjectResourceValidatorImpl implements ProjectResourceValidator {

    private final AmazonS3Properties amazonS3Properties;

    @Override
    public void validateMaxStorageSize(Project project, long newFileLength) {
        BigInteger newLength = project.getStorageSize().add(BigInteger.valueOf(newFileLength));
        if (newLength.compareTo(amazonS3Properties.getMaxFreeStorageSizeBytes()) > 0) {
            throw new DataValidationException("Project with projectId=" + project.getId() + " storage is full");
        }
    }

    @Override
    public void validateDeletePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getCreatedBy().equals(teamMember) && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new NoAccessException("TeamMember with id=" + teamMember.getId() +
                    " has no permissions to delete resource with id=" + resource.getId());
        }
    }
}
