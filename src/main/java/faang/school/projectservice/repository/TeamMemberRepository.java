package faang.school.projectservice.repository;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
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


    public void save(TeamMember teamMember) {
        jpaRepository.save(teamMember);
    }

    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    public List<TeamMember> findByUserId(Long id) {
        return jpaRepository.findByUserId(id);
    }

    public List<TeamMember> findByTeamId(Long teamId) {
        return jpaRepository.findByTeamId(teamId);
    }

    public List<TeamMember> findByProjectId(Long projectId) {
        return jpaRepository.findByProjectId(projectId);
    }

    @Modifying
    public void deleteTeamMember(List<Long> memberId) {
        jpaRepository.deleteAllByIdInBatch(memberId);
    }
}
