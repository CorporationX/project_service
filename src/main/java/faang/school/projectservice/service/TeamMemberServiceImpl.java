package faang.school.projectservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;

    public boolean ifUserIsManager(long teamId) {
        List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userContext.getUserId());        
        Optional<TeamMember> manager = teamMembers.stream()
            .filter(member -> member.getTeam().getId().equals(teamId))
            .filter(member -> member.getRoles().contains(TeamRole.MANAGER))
            .findFirst();
        return manager.isPresent();
    }
}
