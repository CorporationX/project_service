package faang.school.projectservice.repository.adapter;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberRepositoryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Team member with user ID " + userId + " and project ID " + projectId + " not found"));
    }
}
