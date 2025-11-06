package faang.school.projectservice.validation.campaign;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CampaignValidator {

    public void validateUser(Project project, UserContext userContext) {
        List<Long> managersIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getRoles().contains(TeamRole.MANAGER))
                .map(TeamMember::getUserId).toList();

        if (userContext.getUserId() != project.getOwnerId() && !managersIds.contains(userContext.getUserId())) {
            String errorMessage = "Not allowed to create / update campaign for none project member";
            log.error(errorMessage);
            throw new ForbiddenException(errorMessage);
        }
    }
}