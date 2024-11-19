package faang.school.projectservice.jpa;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT id FROM Project WHERE id IN :ids")
    List<Long> findExistingIds(List<Long> ids);

    @Query(
            value = "WITH RECURSIVE search(id) AS (SELECT p.id" +
                    "                                FROM project p" +
                    "                               WHERE p.parent_project_id = :id" +
                    "                               UNION ALL" +
                    "                              SELECT p.id" +
                    "                                FROM project p" +
                    "                                     INNER JOIN search s ON s.id = p.parent_project_id)"  +
                    "SELECT p.*" +
                    "  FROM project p" +
                    "       INNER JOIN search s ON s.id = p.parent_project_id", nativeQuery = true
    )
    List<Project> findAllSubProjectsByParentId(@Param("id") Long id);
}

