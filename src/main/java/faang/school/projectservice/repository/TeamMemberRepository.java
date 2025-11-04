package faang.school.projectservice.repository;

import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
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

    default TeamMember findMentorByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mentor with ID %d was not found".formatted(id)));
    }
}
