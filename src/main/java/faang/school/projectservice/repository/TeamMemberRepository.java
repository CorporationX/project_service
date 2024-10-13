package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.entity.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findById(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public List<TeamMember> findAllById(List<Long> ids) {
        return jpaRepository.findAllById(ids);
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        return jpaRepository.findByUserIdAndProjectId(userId, projectId);
    }
}
