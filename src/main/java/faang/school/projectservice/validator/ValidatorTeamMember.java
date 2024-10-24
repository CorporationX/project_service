package faang.school.projectservice.validator;

import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.TeamMember;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Component;

@Component
public class ValidatorTeamMember {
    public void isMember(TeamMember teamMember, Project project) {
        if (!project.getId().equals(teamMember.getTeam().getProject().getId())) {
            throw new PermissionDeniedDataAccessException("You are not a member of this project", null);
        }
    }
}
