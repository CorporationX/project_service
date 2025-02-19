package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query(
            "SELECT tm FROM TeamMember tm JOIN tm.team t " +
                    "WHERE tm.userId = :userId " +
                    "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    @Query("""
            SELECT COUNT(tm) > 0 FROM TeamMember tm
                 JOIN tm.team t
                 WHERE tm.userId = :userId
                 AND :role MEMBER OF tm.roles
                 AND t.project.id = :projectId
            """)
    boolean checkUserHavingRole(Long userId, Long projectId, TeamRole role);

    List<TeamMember> findByUserId(long userId);
}
