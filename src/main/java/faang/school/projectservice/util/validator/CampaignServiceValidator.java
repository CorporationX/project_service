package faang.school.projectservice.util.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignServiceValidator {
    public void validatePublish(Project project, TeamMember requester) {
        if (!isPublishValid(project, requester)) {
            throw new DataValidationException("In this project there is no such team member");
        }
        if (!requester.getRoles().contains(TeamRole.MANAGER) && !requester.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("In this project, you are neither the manager nor the owner.");
        }
    }

    private boolean isPublishValid(Project project, TeamMember requester) {
        for (Team team : project.getTeams()) {
            List<TeamMember> teamMembers = team.getTeamMembers();
            if (teamMembers.contains(requester)) {
                return true;
            }
        }
        return false;
    }
}
