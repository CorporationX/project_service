package faang.school.projectservice.service.teammember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMember getTeamMemberById(long id) {
        return teamMemberRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Team member with id " + id + " does not exist!"));
    }
}
