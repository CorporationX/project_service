package faang.school.projectservice.service;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;

    public void createMomentCompletedForSubProject(Project subProject) {
        Moment moment = new Moment();
        moment.setName(subProject.getName());
        moment.setDescription("All subprojects completed");

        Set<Long> allProjectMembers = new HashSet<>();
        getProjectMembers(subProject, allProjectMembers);

        moment.setUserIds(new ArrayList<>(allProjectMembers));
        momentRepository.save(moment);
    }

    private void getProjectMembers(Project project, Set<Long> allProjectMembers) {
        List<Team> teams = Optional.ofNullable(project.getTeams()).orElse(Collections.emptyList());
        teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .forEach(allProjectMembers::add);

        List<Project> children = project.getChildren();
        if (children != null) {
            children.forEach(child -> getProjectMembers(child, allProjectMembers));
        }
    }
}
