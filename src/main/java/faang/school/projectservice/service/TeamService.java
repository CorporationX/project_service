package faang.school.projectservice.service;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public List<TeamMember> getAllTeamMember(Long TeamId) {
        Team team = teamRepository.findById(TeamId).orElse(null);
        assert team != null;
        return team.getTeamMembers();
    }
}
