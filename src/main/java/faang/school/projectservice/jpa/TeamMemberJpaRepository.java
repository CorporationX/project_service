package faang.school.projectservice.jpa;

import faang.school.projectservice.model.team.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberJpaRepository extends JpaRepository<TeamMember, Long> {
    @Query(
        "SELECT tm FROM TeamMember tm JOIN tm.team t " +
        "WHERE tm.userId = :userId " +
        "AND t.project.id = :projectId"
    )
    Optional<TeamMember> findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    @Query("SELECT tm FROM TeamMember tm JOIN tm.team t WHERE t.project.id = :projectId")
    List<TeamMember> findAllMembersByProjectId(long projectId);
}
