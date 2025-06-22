package faang.school.projectservice.service.adapter;

import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceAdapter implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public boolean isMember(Long projectId, Long userId) {
        return teamMemberRepository.existsByUserIdAndProjectId(userId, projectId);
    }

    @Override
    public Set<TeamRole> getUserRoles(Long projectId, Long userId) {
        return new HashSet<>(teamMemberRepository.findRolesByUserIdAndProjectId(userId, projectId));
    }

    @Override
    public void save(TeamMember teamMember) {
        teamMemberRepository.save(teamMember);
    }

    @Override
    public void assertOwnerOrManager(Long projectId, Long userId) {
        Set<TeamRole> roles = getUserRoles(projectId, userId);
        if (!roles.contains(TeamRole.OWNER) && !roles.contains(TeamRole.MANAGER)) {
            throw new AccessDeniedException("Need OWNER or MANAGER");
        }
    }
}
