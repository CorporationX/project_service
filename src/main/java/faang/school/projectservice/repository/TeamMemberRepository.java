package faang.school.projectservice.repository;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query(
            "SELECT r FROM TeamMember tm " +
                    "JOIN tm.team t " +
                    "JOIN tm.roles r " +
                    "WHERE tm.userId = :userId " +
                    "AND t.project.id = :projectId"
    )
    List<TeamRole> findRolesByUserIdAndProjectId(Long userId, Long projectId);

    @Query(
        "SELECT tm FROM TeamMember tm JOIN tm.team t " +
        "WHERE tm.userId = :userId " +
        "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    List<TeamMember> findAllByUserIdIn(Collection<Long> userIds);

    @Query(
            "SELECT COUNT(tm) > 0 FROM TeamMember tm JOIN tm.team t " +
                    "WHERE tm.userId = :userId " +
                    "AND t.project.id = :projectId"
    )
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);

    TeamMember team(Team team);
}
