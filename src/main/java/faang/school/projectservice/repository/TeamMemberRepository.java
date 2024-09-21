package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
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

    public Long findAuthorByTeamMemberId(Long id) {
        return jpaRepository.findAuthorByTeamMemberId(id);
    }

    public List<TeamMember> findByRoleAndProject(TeamRole role, Long id) {
        return jpaRepository.findByRoleAndProject(role, id);
    }

    public List<TeamMember> findAllById(List<Long> longs) {
        return jpaRepository.findAllById(longs);
    }
}
