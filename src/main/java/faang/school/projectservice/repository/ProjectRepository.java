package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(
            "SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END " +
                    "FROM Project p " +
                    "WHERE p.ownerId = :ownerId AND p.name = :name"
    )
    boolean existsByOwnerIdAndName(Long ownerId, String name);

    @Query(value = "SELECT * FROM project WHERE name LIKE %:name% AND status = :status", nativeQuery = true)
    List<Project> findAllByNameAndStatus(String name, String status);

    List<Project> findAllByNameContaining(String name);

    List<Project> findAllByStatus(String status);
}

