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

    public TeamMember findById(long id) {
        return jpaRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    }

    public TeamMember save(TeamMember teamMember) {
        return jpaRepository.save(teamMember);
    }

    public TeamMember findByUserIdAndProjectId(long userId, long projectId) {
        return jpaRepository.findByUserIdAndProjectId(userId, projectId).orElseThrow(() ->
                new NotFoundException(String.format("Team member doesn't exist by id: %s", userId)));
    }

    public TeamMember findByTeamMemberUserId(long userId) {
        return jpaRepository.findByTeamMemberUserId(userId).orElseThrow(() ->
                new NotFoundException(String.format("Team member doesn't exist by id: %s", userId)));
    }
}
