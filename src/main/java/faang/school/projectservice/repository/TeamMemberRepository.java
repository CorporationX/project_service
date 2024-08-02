package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public TeamMember findByUserIdAndProjectId(Long userId, Long projectId) {
        TeamMember teamMember = jpaRepository.findByUserIdAndProjectId(userId, projectId);
        if(teamMember == null) {
            throw new EntityNotFoundException("The user is not a member of the project team!");
        }
        else {
            return teamMember;
        }
    }
}
