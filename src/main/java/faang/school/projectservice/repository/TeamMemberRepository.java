package faang.school.projectservice.repository;

import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    @Query("""
            SELECT tm FROM TeamMember tm JOIN tm.team t 
            WHERE tm.userId = :userId 
            AND t.project.id = :projectId
            """
    )
    TeamMember findByUserIdAndProjectId(long userId, long projectId);

    List<TeamMember> findByUserId(long userId);

    default TeamMember getByIdOrThrow(long teamMemberID) {
        return findById(teamMemberID).orElseThrow(
                () -> new EntityNotFoundException(String.format("Team member %d not found", teamMemberID))
        );
    }
}
