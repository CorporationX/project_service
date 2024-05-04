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
    private final TeamMemberJpaRepository teamMemberJpaRepository;

    public TeamMember findById(Long id) {
        return teamMemberJpaRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }


    public void deleteById(Long id) {
        teamMemberJpaRepository.deleteById(id);

    }

    public List<TeamMember> findAllByProjectId(Long projectId) {
        return teamMemberJpaRepository.findAllByProjectId(projectId);
    }
}
