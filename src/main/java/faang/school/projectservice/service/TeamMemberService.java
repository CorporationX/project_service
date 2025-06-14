package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;

import java.util.Set;

public interface TeamMemberService {
    Set<TeamRole> getUserRoles(Long projectId, Long userId);

    boolean isMember(Long projectId, Long userId);

    void save(TeamMember teamMember);

    void assertOwnerOrManager(Long projectId, Long userId);
}
