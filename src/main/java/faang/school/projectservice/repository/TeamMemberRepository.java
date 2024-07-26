package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public boolean existAllByIds(Collection<Long> teamMemberIds) {
        return jpaRepository.countAllByIds(teamMemberIds) == teamMemberIds.size();
    }

    public List<Long> findIdsByProjectIds(Collection<Long> projectIds) {
        return jpaRepository.findIdsByProjectIds(projectIds);
    }
}
