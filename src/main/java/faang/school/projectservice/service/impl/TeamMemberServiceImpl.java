package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public List<TeamMember> findAllById(List<Long> ids) {
        return teamMemberRepository.findAllById(ids);
    }

    @Override
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

    @Override
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
