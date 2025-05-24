package faang.school.projectservice.repository.adapter.teammember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamMemberRepoAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getByUserId(Long userId) {
        if (userId == null) {
            throw new EntityNotFoundException("UserId must not be null");
        }
        return teamMemberRepository.findByUserId(userId);
    }

    public List<TeamMember> getTeamMembersByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("userIds must not be null");
        }
        return teamMemberRepository.findAllByUserIdIn(userIds);
    }
}
