package faang.school.projectservice.repository.adapter.teammember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamMemberRepoAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getAllTeamMembersByUserId(Long userId) {
        return teamMemberRepository.findByUserId(userId);
    }
}
