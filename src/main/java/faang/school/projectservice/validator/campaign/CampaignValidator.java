package faang.school.projectservice.validator.campaign;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.TeamRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CampaignValidator {

    public void validateCampaignAuthor(long authorId, Project project) {
        project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(member -> member.getUserId().equals(authorId))
                .filter(member -> member.getRoles().contains(TeamRole.MANAGER) ||
                        member.getRoles().contains(TeamRole.OWNER))
                .findAny()
                .orElseThrow(() -> new DataValidationException("User is not an owner or manager of the project"));
    }
}
