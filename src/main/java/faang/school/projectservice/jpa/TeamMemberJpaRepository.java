package faang.school.projectservice.jpa;

import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.enums.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {
    @Query(
            "SELECT tm FROM TeamMember tm JOIN tm.team t " +
                    "WHERE tm.userId = :userId " +
                    "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    @Query("select distinct tm.roles from TeamMember tm " +
            "join tm.team t " +
            "where t.project.id = :projectId and tm.userId = :userId")
    List<TeamRole> findRolesByProjectIdAndUserId(Long projectId, Long userId);

    List<TeamMember> findByUserId(long userId);

    @Query("""
            SELECT count(tm) FROM TeamMember tm
            WHERE tm.id IN (:teamMemberIds)
                        """
    )
    long countAllByIds(Collection<Long> teamMemberIds);

    @Query("""
            SELECT tm.id FROM TeamMember tm
            JOIN Team t ON tm.team.id = t.id
            JOIN Project p ON t.project.id = p.id
            WHERE p.id IN (:projectIds)
                        """
    )
    List<Long> findIdsByProjectIds(Collection<Long> projectIds);
}
