package faang.school.projectservice.service.team;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMember(long id) {
        return teamMemberRepository.findById(id);
    }

    public List<TeamMember> getAllTeamMembersByIds(List<Long> ids) {
        return teamMemberRepository.findAllByIds(ids);
    }
}
