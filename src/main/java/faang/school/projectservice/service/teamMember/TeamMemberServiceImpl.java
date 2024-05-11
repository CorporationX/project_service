package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeamMemberServiceImpl implements TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public TeamMember findById(Long id) {
        return teamMemberRepository.findById(id);
    }

    @Override
    public TeamMember findByUserIdAndProjectId(Long id, Long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(id, projectId);
        if (teamMember == null) {
            throw new EntityNotFoundException(String.format("Team member with id: %d is not member of project with id: %d", id, projectId));
        }
        return teamMember;
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        teamMemberRepository.deleteById(id);
    }
}