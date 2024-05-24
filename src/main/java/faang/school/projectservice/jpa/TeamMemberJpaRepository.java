package faang.school.projectservice.jpa;

import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {
    @Query(
            "SELECT tm FROM TeamMember tm " +
            "JOIN Team t ON tm.team.id = t.id " +
            "WHERE tm.userId = :userId " +
            "AND t.project = :projectId"
    )
    Optional<TeamMember> findByUserIdAndProjectId(long userId, long projectId);

    @Query(
            "SELECT tm FROM TeamMember tm " +
            "WHERE tm.userId = :userId"
    )
    Optional<TeamMember> findByTeamMemberUserId(long userId);

    List<TeamMember> findByUserId(long userId);
}
