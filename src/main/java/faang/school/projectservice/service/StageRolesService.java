package faang.school.projectservice.service;

import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageRolesService {

    private final StageRolesRepository stageRolesRepository;
    private final StageInvitationService stageInvitationService;
    private final TeamMemberService teamMembersService;

    public void saveAll(List<StageRoles> stageRolesList) {
        stageRolesRepository.saveAll(stageRolesList);
    }

    public void getExecutorsForRole(Stage stage, StageRoles stageRoles) {
        List<TeamMember> executorsWithTheRole = teamMembersService.getTeamMembersWithTheRole(stage, stageRoles);

        int requiredNumberOfInvitation = stageRoles.getCount() - executorsWithTheRole.size();

        List<TeamMember> projectMembersWithTheSameRole = new ArrayList<>();
        if (requiredNumberOfInvitation > 0) {
            projectMembersWithTheSameRole =
                    teamMembersService.getProjectMembersWithTheSameRole(stage, stageRoles);
        }

        if (projectMembersWithTheSameRole.size() < requiredNumberOfInvitation) {
            projectMembersWithTheSameRole.forEach(teamMember -> stageInvitationService
                    .createStageInvitation(teamMember, stage, stageRoles));

        } else {
            projectMembersWithTheSameRole.stream()
                    .limit(requiredNumberOfInvitation)
                    .forEach(teamMember -> stageInvitationService.createStageInvitation(teamMember, stage, stageRoles));
        }
    }
}
