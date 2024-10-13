package faang.school.projectservice.jpa;

import faang.school.projectservice.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Collection;


@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(
            value = "WITH RECURSIVE search(id) AS (" +
                    "    SELECT  p.id" +
                    "    FROM project p" +
                    "    WHERE p.parent_project_id = :projectId" +
                    "    UNION ALL" +
                    "    SELECT p.id" +
                    "    FROM project p" +
                    "             INNER JOIN search s ON s.id = p.parent_project_id" +
                    ")" +
                    "SELECT DISTINCT project.id, name, description, parent_project_id, storage_size, max_storage_size, owner_id, created_at, updated_at, status, visibility, cover_image_id\n" +
                    "FROM project inner join search on search.id = project.id", nativeQuery = true
    )
    List<Project> getAllSubprojectsForProjectId(@Param("projectId") Long projectId);

    @Query("""
            SELECT DISTINCT p FROM Project p
            JOIN Team t ON t.project.id = p.id
            JOIN TeamMember tm ON tm.team.id = t.id
            WHERE tm.id IN (:teamMemberIds)
            """
    )
    List<Project> findAllDistinctByTeamMemberIds(Collection<Long> teamMemberIds);
}

