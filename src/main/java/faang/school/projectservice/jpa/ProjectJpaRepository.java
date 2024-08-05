package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT project.* FROM project
            LEFT JOIN team ON team.project_id = project.id
            LEFT JOIN team_member ON team_member.team_id = team.id
            WHERE project.visibility = 'PUBLIC' OR team_member.id = ?1 OR project.owner_id = ?1
            """)
    Stream<Project> findAllAvailableProjectsByUserId(Long userId);

    @Query(nativeQuery = true, value = """
            SELECT project.* FROM project
            LEFT JOIN team ON team.project_id = project.id
            LEFT JOIN team_member ON team_member.team_id = team.id
            WHERE project.id = ?2 AND (project.visibility = 'PUBLIC' OR team_member.id = ?1 OR project.owner_id = ?1)
            """)
    Optional<Project> findAvailableByUserIdAndProjectId(Long userId, Long projectId);

}


