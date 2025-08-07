package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("""
            SELECT tm FROM TeamMember tm JOIN tm.team t
            WHERE tm.userId = :userId
            AND t.project.id = :projectId
            """)
    Optional<TeamMember> findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    @Query("""
            SELECT COUNT(tm) > 0
            FROM TeamMember tm
            JOIN tm.team t
            WHERE tm.userId = :userId
                AND t.project.id = :projectId
                AND :role MEMBER OF tm.roles
            """)
    boolean isUserHasRole(long userId, long projectId, TeamRole role);

    default void removeMemberFromProjectOrThrow(long userId, long projectId) {
        TeamMember member = findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with userId = " + userId + " and projectId = " + projectId));
        delete(member);
    }
}
