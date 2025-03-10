package faang.school.projectservice.validator.project;

import faang.school.projectservice.exception.CampaignCannotBeCreated;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateUserOwnerOrManager(Project project, Long userId) {
        if (!project.getOwnerId().equals(userId) &&
                !teamMemberRepository.checkUserHavingRole(userId, project.getId(), TeamRole.MANAGER)) {
            throw new CampaignCannotBeCreated("User with id " + userId + " has not rights for this operation");
        }
    }
}
