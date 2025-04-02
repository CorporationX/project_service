package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PresentationValidator {

    public void validateUserInProject(Long userId, Project project) {
        if (!isUserParticipatedInProject(userId, project)) {
            throw new IllegalArgumentException("User with id "
                    + userId + " not in project "
                    + project.getId() + " at this moment");
        }
    }

    public boolean isUserParticipatedInProject(Long userId, Project project) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId).anyMatch(usrId -> usrId.equals(userId));
    }

    public boolean isProjectPublic(Project project) {
        return ProjectVisibility.PUBLIC.equals(project.getVisibility());
    }

    public void validateUserIsOwner(long userId, Project project) {
        if (userId != project.getOwnerId()) {
            throw new IllegalArgumentException("Only the project owner can request a presentation!");
        }
    }
}
