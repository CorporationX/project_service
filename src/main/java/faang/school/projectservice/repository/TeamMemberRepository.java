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

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        TeamMember teamMember = jpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            throw new EntityNotFoundException(String.format("Team member doesn't exist by User ID: %s and Project ID: %s", userId, projectId));
        }
        return teamMember;
    }
}
