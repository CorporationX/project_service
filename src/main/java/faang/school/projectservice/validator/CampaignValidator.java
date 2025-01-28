package faang.school.projectservice.validator;

import faang.school.projectservice.exeption.NotAccessRoleCompaignException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignValidator {
    public void validateCampaignAuthor(long authorId, Project project) {

        project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream())
                .filter(teamMember -> teamMember.getUserId().equals(authorId))
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.MANAGER) ||
                        teamMember.getRoles().contains(TeamRole.OWNER))
                .findAny()
                .orElseThrow(() -> new NotAccessRoleCompaignException("Role User is not an owner or manager of the project"));
    }
}

