package faang.school.projectservice.validator.projectservice;

import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;


@Component
public class ProjectParticipantValidatorByVisibility {

    public boolean isUserParticipantInProject(Project project, Long userId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getUserId().equals(userId));
    }
}
