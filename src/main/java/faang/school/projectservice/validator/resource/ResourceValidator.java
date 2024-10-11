package faang.school.projectservice.validator.resource;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Resource;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.TeamRole;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class ResourceValidator {

    public void validateStorage(Project project, BigInteger newStorageSize) {
        if (newStorageSize.compareTo(project.getMaxStorageSize()) > 0) {
            throw new DataValidationException("Storage size is exceed");
        }
    }

    public void validateResourceOwner(Resource resource, TeamMember member) {
        if (!resource.getCreatedBy().equals(member) && !member.getRoles().contains(TeamRole.MANAGER)) {
            throw new DataValidationException("Resource can be deleted only by creator or project owner!");
        }
    }
}
