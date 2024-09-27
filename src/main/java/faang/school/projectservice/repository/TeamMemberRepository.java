package faang.school.projectservice.repository;

import faang.school.projectservice.exception.IllegalEntityException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public void save(TeamMember teamMember) {
        jpaRepository.save(teamMember);
    }

    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    public List<TeamMember> findAllById(List<Long> longs) {
        return jpaRepository.findAllById(longs);
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        return Optional.ofNullable(jpaRepository.findByUserIdAndProjectId(userId, projectId))
                .orElseThrow(() -> new IllegalEntityException("TeamMember with id userId %d and projectId %d does not exist"
                        .formatted(userId, projectId)));
    }
}
