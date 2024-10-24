package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;

    @Override
    public TeamMemberDto getTeamMember(long creatorId, long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectIdWithRole(creatorId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Failed to find team member with creator id %d and project id %d".formatted(creatorId, projectId)
                ));
        return teamMemberMapper.toDto(teamMember);
    }
}
