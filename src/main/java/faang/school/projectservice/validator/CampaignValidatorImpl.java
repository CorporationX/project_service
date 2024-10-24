package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class CampaignValidatorImpl implements CampaignValidator {

    @Override
    public void validationCampaignCreator(TeamMemberDto teamMember, ProjectDto project) {
        if (!hasManagerRole(teamMember) || !isOwner(teamMember, project)) {
            throw new DataValidationException("User with id %d cannot create".formatted(teamMember.getUserId()));
        }
    }

    private static boolean isOwner(TeamMemberDto teamMember, ProjectDto project) {
        return project.getOwnerId().equals(teamMember.getUserId());
    }

    private boolean hasManagerRole(TeamMemberDto teamMember) {
        return teamMember.getRoles()
                .stream()
                .anyMatch(role -> role.equals(TeamRole.MANAGER));
    }
}
