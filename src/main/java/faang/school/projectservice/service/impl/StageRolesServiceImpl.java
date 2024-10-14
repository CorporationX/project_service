package faang.school.projectservice.service.impl;

import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;
import faang.school.projectservice.service.StageInvitationService;
import faang.school.projectservice.service.StageRolesService;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StageRolesServiceImpl implements StageRolesService {

    private final StageRolesRepository stageRolesRepository;
    private final StageInvitationService stageInvitationService;
    private final TeamMemberService teamMembersService;

    @Override
    public void saveAll(List<StageRoles> stageRolesList) {
        stageRolesRepository.saveAll(stageRolesList);
    }

    @Override
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
