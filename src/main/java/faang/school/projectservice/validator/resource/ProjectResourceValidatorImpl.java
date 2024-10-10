package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.NoAccessException;
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

    private final AmazonS3Properties properties;

    @Override
    public void validateMaxStorageSize(Project project, long fileSize) {
        BigInteger currentStorageSize = project.getStorageSize().add(BigInteger.valueOf(fileSize));
        if (currentStorageSize.compareTo(properties.getMaxFreeStorageSizeBytes()) > 0) {
            throw new DataValidationException(String.format("Project with ID %s. Limit reached. Storage is full.", project.getId()));
        }
    }

    @Override
    public void validateDeletePermission(TeamMember teamMember, Resource resource) {
        if (!resource.getCreatedBy().equals(teamMember) && !teamMember.getRoles().contains(TeamRole.MANAGER)) {
            throw new NoAccessException(String.format("Team member '%s' does not have permission to delete resource with ID %s.",
                    teamMember, resource.getId()));
        }
    }
}
