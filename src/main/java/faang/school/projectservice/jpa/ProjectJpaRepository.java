package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT p.* FROM project p
            JOIN team t ON p.id = t.project_id
            JOIN team_member tm on t.id = tm.team_id
            WHERE tm.user_id = :teamMemberId
                  """)
    List<Project> findProjectByTeamMember(long teamMemberId);
}

