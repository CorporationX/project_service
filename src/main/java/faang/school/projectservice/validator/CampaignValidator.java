package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.dto.UserDto;
import faang.school.projectservice.model.enums.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CampaignValidator {
    private final UserServiceClient userServiceClient;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final ProjectRepository projectRepository;

    public void validateUser(long userId) {
        if (userId <= 0) {
            throw new DataValidationException(String.format("User's id can't be equal %d", userId));
        }
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new DataValidationException(String.format("The user must exist in the system, userId = %d", userId));
        }
    }

    public void validateUserIsCreator(long userId, long creatorId) {
        if (userId != creatorId) {
            throw new DataValidationException("The campaign creator's ID does not match the user's ID");
        }
    }

    public void validateIdFromPath(long id, Long meetDtoId) {
        if (meetDtoId != null && meetDtoId != id) {
            throw new DataValidationException("The ID in the path must match the ID in the DTO");
        }
    }

    public void validateManagerOrOwner(long userId, long projectId) {
        List<TeamRole> roles = teamMemberJpaRepository.findRolesByProjectIdAndUserId(projectId, userId);
        if (roles.contains(TeamRole.OWNER) || roles.contains(TeamRole.MANAGER)) {
            return;
        }
        throw new DataValidationException("Only the owner or manager can create, modify, or delete a campaign");
    }

    public void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException(String.format("Project with id = %d doesn't exist", projectId));
        }
    }

    public void validateCreatorIsTheSame(Long savedCreatedBy, Long createdByFromDto) {
        if (!savedCreatedBy.equals(createdByFromDto)) {
            throw new DataValidationException("You can't change campaign's creator while executing method update");
        }
    }
}
