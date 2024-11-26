package faang.school.projectservice.service.team_member;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private TeamMemberRepository teamMemberRepository;

    public TeamMember findById(Long id){
        return teamMemberRepository.findById(id);
    }

    public boolean existedTeamMember(Long id){
        return teamMemberRepository.existedTeamMember(id);
    }
}
