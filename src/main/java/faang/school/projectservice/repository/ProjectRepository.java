package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(nativeQuery = true, value = """
            SELECT pg.file_key
            FROM project_gallery pg
            WHERE pg.project_id = :projectId""")
    List<String> findFileKeysByProjectId(@Param("projectId") Long projectId);

    @Query(nativeQuery = true, value = """
            SELECT pg.file_key
            FROM project_gallery pg
            WHERE pg.project_id = :projectId""")
    Page<String> findFileKeysByProjectId(@Param("projectId") Long projectId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(pg.file_key)
            FROM project_gallery pg
            WHERE pg.project_id = :projectId""")
    int countFilesByIdProjectId(@Param("projectId") Long projectId);
}