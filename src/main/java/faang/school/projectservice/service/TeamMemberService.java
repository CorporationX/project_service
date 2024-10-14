package faang.school.projectservice.service;

import faang.school.projectservice.model.entity.Stage;
import faang.school.projectservice.model.entity.StageRoles;
import faang.school.projectservice.model.entity.TeamMember;

import java.util.List;

public interface TeamMemberService {
    List<TeamMember> findAllById(List<Long> ids);

    List<TeamMember> getTeamMembersWithTheRole(Stage stage, StageRoles stageRoles);

    List<TeamMember> getProjectMembersWithTheSameRole(Stage stage, StageRoles stageRoles);
}
