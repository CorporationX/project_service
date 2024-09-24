package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> findAllById(List<Long> ids) {
        return teamMemberRepository.findAllById(ids);
    }

    public List<TeamMember> getTeamMembersWithTheRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = new ArrayList<>();
        stage.getExecutors().forEach(executor -> {
            if (executor.getRoles()
                    .contains(stageRoles.getTeamRole())) {
                executorsWithTheRole.add(executor);
            }
        });
        return executorsWithTheRole;
    }

    public List<TeamMember> getProjectMembersWithTheSameRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
        stage.getProject()
                .getTeams()
                .forEach(team ->
                        projectMembersWithTheSameRole.addAll(
                                team.getTeamMembers()
                                        .stream()
                                        .filter(teamMember ->
                                                !teamMember.getStages().contains(stage))
                                        .filter(teamMember ->
                                                teamMember.getRoles()
                                                        .contains(stageRoles.getTeamRole()))
                                        .toList()));
        return projectMembersWithTheSameRole;
    }
}
