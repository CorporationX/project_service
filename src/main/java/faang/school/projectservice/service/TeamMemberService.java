package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public TeamMember getTeamMemberByUserIdAndProjectId(Long userId, Long projectId) {
        return teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public List<TeamMember> getTeamMembersByIds(List<Long> memberIds) {
        return teamMemberJpaRepository.findAllById(memberIds);
    }
}