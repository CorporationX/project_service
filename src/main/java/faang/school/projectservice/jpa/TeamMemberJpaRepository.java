package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {
    @Query(
        "SELECT tm FROM TeamMember tm JOIN tm.team t " +
        "WHERE tm.userId = :userId " +
        "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    @Query(nativeQuery = true, value = """
            SELECT tm.* from team_member tm
            Join team ON tm.team_id = team.id
            WHERE team.project_id = :projectId
            """)
    List<TeamMember> findAllByProjectId(long projectId);
}
