package faang.school.projectservice.adapter;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberRepositoryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember save(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User с id: %s not found!", id)));
    }

    public TeamMember findByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
