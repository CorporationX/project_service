package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import feign.Param;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {
    @Query(
        "SELECT tm FROM TeamMember tm JOIN tm.team t " +
        "WHERE tm.userId = :userId " +
        "AND t.project.id = :projectId"
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    @Query("SELECT tm.userId FROM TeamMember tm WHERE tm.id = :teamMemberId")
    Long findAuthorByTeamMemberId(@Param("teamMemberId") Long teamMemberId);


    @Query("SELECT tm FROM TeamMember tm JOIN tm.team t WHERE t.project.id = :projectId AND :role MEMBER OF tm.roles")
    List<TeamMember> findByRoleAndProject(@Param("role") TeamRole role, @Param("projectId") Long projectId);

}
