package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.model.TeamMember;

public interface TeamMemberService {
    TeamMember findById(Long id);
    TeamMember findByUserIdAndProjectId(Long id, Long projectId);
    void deleteById(Long id);
}
