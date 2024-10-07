package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamMemberRepository {
    private final TeamMemberJpaRepository jpaRepository;

    public TeamMember findByUserIdAndProjectId(Long userId, Long projectId) {
        return jpaRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public TeamMember findByIdOrThrow(Long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public TeamMember save(TeamMember teamMember) {
        return jpaRepository.save(teamMember);
    }

    public List<TeamMember> saveAll(List<TeamMember> teamMembers) {
        return jpaRepository.saveAll(teamMembers);
    }

    public List<TeamMember> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    public void deleteAll(List<TeamMember> members) {
        jpaRepository.deleteAll(members);
    }
}
