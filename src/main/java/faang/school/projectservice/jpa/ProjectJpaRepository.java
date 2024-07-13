package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    // todo check without ON
    // ON t.project.id = p.id
    // ON tm.team.id = t.id
    @Query("""
            SELECT DISTINCT p FROM Project p
            JOIN Team t
            JOIN TeamMember tm
            WHERE tm.id IN (:teamMemberIds)
            """
    )
    List<Project> findAllDistinctByTeamMemberIds(Collection<Long> teamMemberIds);
}

