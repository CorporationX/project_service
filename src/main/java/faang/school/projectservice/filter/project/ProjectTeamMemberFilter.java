package faang.school.projectservice.filter.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ProjectTeamMemberFilter implements DefaultProjectFilter {

    private final UserContext userContext;

    @Override
    public Stream<Project> apply(Stream<Project> projects) {
        return projects.filter(this::isUserPartOfAnyTeam);
    }

    private boolean isUserPartOfAnyTeam(Project project) {
        return project.getTeams() != null && project.getTeams().stream().anyMatch(this::isUserATeamMember);
    }

    private boolean isUserATeamMember(Team team) {
        return team.getTeamMembers() != null && team.getTeamMembers().stream().anyMatch(member ->
                member.getUserId() != null && member.getUserId().equals(userContext.getUserId()));
    }
}
