package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

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

    Project getProjectById(Long projectId);
}
