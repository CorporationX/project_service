package faang.school.projectservice.service;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMember(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException("TeamMember not found"));
    }

    public List<TeamMember> findAllByIds(List<Long> teamMemberIds) {
        return teamMemberRepository.findAllById(teamMemberIds);
    }
}
