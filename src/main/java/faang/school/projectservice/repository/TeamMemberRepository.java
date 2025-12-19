package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("""
            SELECT tm FROM TeamMember tm JOIN tm.team t 
            WHERE tm.userId = :userId 
            AND t.project.id = :projectId
            """
    )
    Optional<TeamMember> findByUserIdAndProjectId(
            @Param("userId") long userId,
            @Param("projectId") long projectId
    );

    @Query("SELECT tm FROM TeamMember tm JOIN tm.team t WHERE tm.id = :id AND t.project.id = :projectId")
    Optional<TeamMember> findByIdAndProjectId(
            @Param("id") Long id,
            @Param("projectId") Long projectId
    );

    List<TeamMember> findByUserId(long userId);
}
