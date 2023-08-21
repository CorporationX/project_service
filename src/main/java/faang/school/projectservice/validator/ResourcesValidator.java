package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ProjectStorageCapacityExceededException;
import faang.school.projectservice.exception.UserNorAccessRightDeleteException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResourcesValidator {
    private static final int MAX_PROJECT_FILE_SIZE = 2_097_152_000;

    public void checkStorageCapacity(long newStorageCapacity) {
        if (newStorageCapacity > MAX_PROJECT_FILE_SIZE) {
            log.error("throw ProjectStorageCapacityExceededException");
            throw new ProjectStorageCapacityExceededException("Project storage capacity exceeded");
        }
    }

    public void checkRightsToDelete(Resource resource, Project project, long userId) {
        if (resource.getCreatedBy().getUserId() != userId) {
            throw new UserNorAccessRightDeleteException("The user does not have the right to delete");
        }

        if (project.getOwnerId() != userId) {
            throw new UserNorAccessRightDeleteException("The user does not have the right to delete");
        }
    }

    public void checkTeamMemberInProject(TeamMember teamMember) {
        if (teamMember == null) {
            throw new DataValidationException("Team member is not in the project");
        }
    }
}
