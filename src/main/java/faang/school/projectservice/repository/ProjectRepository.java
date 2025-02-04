package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(value = "SELECT EXISTS (" +
            "SELECT 1 " +
            "FROM team_member  tm " +
            "INNER JOIN team ON tm.team_id = team.id " +
            "INNER JOIN project ON team.project_id = project.id " +
            "WHERE project.id = :projectId AND tm.user_id = :userId)",
            nativeQuery = true)
    boolean isUserMemberOfProject(Long projectId, Long userId);
}

