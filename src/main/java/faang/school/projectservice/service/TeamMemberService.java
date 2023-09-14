package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public TeamMember getTeamMemberByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
    }
}