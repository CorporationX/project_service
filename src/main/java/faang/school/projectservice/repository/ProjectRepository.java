package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    Optional<Project> findById(Long id);

    @Query(nativeQuery = true, value = """
                SELECT *
                FROM project
                WHERE parent_project_id = :parentProjectId
            """)
    List<Project> findByParentId(@Param("parentProjectId") Long parentProjectId);
}
