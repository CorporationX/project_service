package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.team.TeamMember;
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

    public Optional<TeamMember> findByUserIdAndProjectId(Long userId, Long projectId) {
        return jpaRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public List<TeamMember> findAllMembersByProjectId(Long projectId) {
        return jpaRepository.findAllMembersByProjectId(projectId);
    }

    public TeamMember save(TeamMember teamMember) {
        return jpaRepository.save(teamMember);
    }

    public void delete(TeamMember teamMember) {
        jpaRepository.delete(teamMember);
    }
}
